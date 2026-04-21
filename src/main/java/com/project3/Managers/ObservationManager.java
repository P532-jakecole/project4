package com.project3.Managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project3.Command.CreatePatientCommand;
import com.project3.Command.RecordObservationCommand;
import com.project3.Command.RejectObservationCommand;
import com.project3.CommandLog;
import com.project3.DataTypes.*;
import com.project3.Factory.ObservationFactory;
import com.project3.OrderAccess;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

@Service
public class ObservationManager {
    private final OrderAccess orderAccess;
    private final ObservationFactory observationFactory;
    private ApplicationEventPublisher publisher;
    private final CommandLog commandLog;
    private final ObjectMapper mapper;


    public ObservationManager(OrderAccess orderAccess, ObservationFactory observationFactory, ApplicationEventPublisher publisher, CommandLog commandLog) {
        this.orderAccess = orderAccess;
        this.observationFactory = observationFactory;
        this.publisher = publisher;
        this.commandLog = commandLog;
        mapper = new ObjectMapper();
    }

    // Input string is [ patient , phenomenonTypes , value, unit, (optional)protocolId, staff, applicableTime ]
    public int recordMeasurement(Object[] inputs) throws Exception {
        String staff = inputs[5].toString();
        RecordObservationCommand recordObservation = new RecordObservationCommand(inputs, observationFactory, orderAccess, staff, "measurement");
        recordObservation.execute();
        int commandId = commandLog.addCommandLog(CommandType.RecordObservation, mapper.writeValueAsString(recordObservation), recordObservation.timestamp, recordObservation.staff, recordObservation.observationId);
        publisher.publishEvent(recordObservation);
        return commandId;
    }

    // Input string is [ patientId, phenomenonId, presence, (optional) protocolId, applicabilityTime, staff ]
    public int recordCategoryObservation(Object[] inputs) throws Exception {
        String staff = inputs[5].toString();
        RecordObservationCommand recordObservation = new RecordObservationCommand(inputs, observationFactory, orderAccess, staff, "categoryobservation");
        recordObservation.execute();
        int commandId = commandLog.addCommandLog(CommandType.RecordObservation, mapper.writeValueAsString(recordObservation), recordObservation.timestamp, recordObservation.staff, recordObservation.observationId);
        publisher.publishEvent(recordObservation);
        return commandId;
   }

    public int rejectObservation(Integer id, String reason, String staff) throws JsonProcessingException {
        RejectObservationCommand rejectObservation = new RejectObservationCommand(id, reason, orderAccess, staff);
        rejectObservation.execute();
        int commandId = commandLog.addCommandLog(CommandType.RejectObservation, mapper.writeValueAsString(rejectObservation), rejectObservation.timestamp, rejectObservation.staff, rejectObservation.observationId);
        publisher.publishEvent(rejectObservation);
        return commandId;
   }


   public int undoCommand(Integer id, String user) throws JsonProcessingException {
        CommandLogEntry entry = commandLog.getCommandLogById(id);
        if(entry == null){
            throw new Error("Command not found");
        }
        String staff = entry.getUser();
        user = user.replace("\"", "");

        if(!staff.equals(user) || entry.getCommandType() == CommandType.Undo) {
            return id;
        }

        String payload = entry.getPayload();
        CommandType type = entry.getCommandType();

       Map<String, Object> map = mapper.readValue(payload, Map.class);


        if(type == CommandType.RecordObservation){
            List<Object> inputList = (List<Object>) map.get("inputs");
            inputList.add(entry.getObservationId());
            Object[] inputs = inputList.toArray(new Object[0]);
            staff = (String) map.get("staff");

            RecordObservationCommand command = new RecordObservationCommand(inputs, observationFactory, orderAccess, staff, "undo");
            command.undo();
            int commandId = commandLog.addCommandLog(CommandType.Undo, payload, command.timestamp, command.staff, command.observationId);
            entry.setUndone(true);
            commandLog.updateCommandLogEntry(entry);
            return commandId;
        }else if(type == CommandType.RejectObservation){
            Integer obsId = (Integer) map.get("observationId");
            String reason = (String) map.get("reason");
            staff = (String) map.get("staff");

            RejectObservationCommand command = new RejectObservationCommand(obsId, reason, orderAccess, staff);
            command.undo();
            int commandId = commandLog.addCommandLog(CommandType.Undo, payload, command.timestamp, command.staff, command.observationId);
            entry.setUndone(true);
            commandLog.updateCommandLogEntry(entry);
            return commandId;
        }else if(type == CommandType.CreatePatient){
            String name = (String) map.get("name");
            Date dob = (Date) map.get("dob");
            String note = (String) map.get("note");
            Object[] inputs = new Object[3];
            inputs[0] = name;
            inputs[1] = dob;
            inputs[2] = note;
            staff = (String) map.get("staff");

            CreatePatientCommand command = new CreatePatientCommand(inputs, orderAccess, staff);
            command.undo();
            int commandId = commandLog.addCommandLog(CommandType.Undo, payload, command.timestamp, command.staff, -1);
            entry.setUndone(true);
            commandLog.updateCommandLogEntry(entry);
            return commandId;
        }

        throw new Error("Command type not found");
   }

    public ArrayList<PhenomenonType> listPhenomenonTypes(){
        return orderAccess.findAllPhenomenonTypes();
    }

    // inputs: [ name, kind, allowedUnits, (optional) lowerBound, (optional) upperBound ]
    @Transactional
    public void createPhenomenonType(Object[] inputs){
        PhenomenonType pt = new PhenomenonType();
        pt.setName((String) inputs[0]);

        switch (inputs[1].toString()) {
            case "QUANTITATIVE":
                pt.setKind(PhenomenonKind.QUANTITATIVE);
                List<String> unitsList = (List<String>) inputs[2];
                pt.setAllowedUnits(unitsList);
                pt.setNormalMin(Double.parseDouble(String.valueOf(inputs[3])));
                pt.setNormalMax(Double.parseDouble(String.valueOf(inputs[4])));
                orderAccess.addPhenomenonType(pt);
                break;
            case "QUALITATIVE":

                pt.setKind(PhenomenonKind.QUALITATIVE);
                List<?> rawList = (List<?>) inputs[2];
                orderAccess.addPhenomenonType(pt);


                List<Phenomenon> phenomenons = new ArrayList<>();

                for (Object obj : rawList) {

                    LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) obj;

                    String name = (String) map.get("name");
                    Object parentIdObj = map.get("parentId");


                    Phenomenon phen = new Phenomenon();
                    phen.setName(name);
                    phen.setPhenomenonType(pt);

                    if (parentIdObj != null) {
                        Integer parentId = Integer.parseInt(parentIdObj.toString());
                        phen.setParentConcept(orderAccess.getPhenomenaById(parentId));
                    }

                    orderAccess.addPhenomenon(phen);

                }
                break;
        }
    }


//    public void createPhenomenonType(Object[] inputs) {
//        Map<Integer, Phenomenon> parentCache = Collections.emptyMap();
//
//        if (inputs[1].toString().equals("QUALITATIVE")) {
//            List<?> rawList = (List<?>) inputs[2];
//            Set<Integer> parentIds = rawList.stream()
//                    .map(obj -> (LinkedHashMap<String, Object>) obj)
//                    .map(map -> map.get("parentId"))
//                    .filter(Objects::nonNull)
//                    .map(id -> Integer.parseInt(id.toString()))
//                    .collect(Collectors.toSet());
//
//            // Read completes here, no transaction open yet
//            parentCache = orderAccess.getPhenomenaByIds(parentIds)
//                    .stream()
//                    .collect(Collectors.toMap(p -> p.getId().intValue(), p -> p));
//        }
//
//        persistPhenomenonType(inputs, parentCache);
//    }
//
//
//    public void persistPhenomenonType(Object[] inputs, Map<Integer, Phenomenon> parentCache) {
//        PhenomenonType pt = new PhenomenonType();
//        pt.setName((String) inputs[0]);
//
//        switch (inputs[1].toString()) {
//            case "QUANTITATIVE":
//                pt.setKind(PhenomenonKind.QUANTITATIVE);
//                List<String> unitsList = (List<String>) inputs[2];
//                pt.setAllowedUnits(unitsList);
//                pt.setNormalMin(Double.parseDouble(String.valueOf(inputs[3])));
//                pt.setNormalMax(Double.parseDouble(String.valueOf(inputs[4])));
//                break;
//
//            case "QUALITATIVE":
//                pt.setKind(PhenomenonKind.QUALITATIVE);
//                List<?> rawList = (List<?>) inputs[2];
//
//                List<Phenomenon> phenomenons = new ArrayList<>();
//                for (Object obj : rawList) {
//                    LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) obj;
//                    String name = (String) map.get("name");
//                    Object parentIdObj = map.get("parentId");
//
//                    Phenomenon phen = new Phenomenon();
//                    phen.setName(name);
//                    phen.setPhenomenonType(pt);
//
//                    if (parentIdObj != null) {
//                        Integer parentId = Integer.parseInt(parentIdObj.toString());
//                        phen.setParentConcept(parentCache.get(parentId));
//                    }
//                    phenomenons.add(phen);
//                }
//
//                pt.setPhenomena(phenomenons);
//                orderAccess.addPhenomenonType(pt);
//                orderAccess.addAllPhenomena(phenomenons);
//                return;
//        }
//
//        orderAccess.addPhenomenonType(pt);
//    }

    // List<String> concepts, List<Double> weights, String name, String product, String strategy

    public List<Phenomenon> getPhenomenon(){
        return orderAccess.getPhenomenon();
    }

    @Transactional
    public void createAssociativeFunction(Object[] input){

        List<String> concepts = (List<String>) input[0];
        List<Double> weights = ((List<?>) input[1])
                .stream()
                .map(w -> Double.parseDouble(w.toString()))
                .toList();

        String name = (String) input[2];
        String product = (String) input[3];
        String strategy = (String) input[4];
        Double threshold = 0.0;
        if(input.length > 5){
            threshold = Double.parseDouble(input[5].toString());
        }


        AssociativeFunction f = new AssociativeFunction();

        List<ArgumentWeight> arguments = new ArrayList<>();
        for(int i = 0; i < concepts.size(); i++){
            ArgumentWeight argWeight = new ArgumentWeight();
            argWeight.setWeight(weights.get(i));
            argWeight.setConcept(concepts.get(i));
            argWeight.setFunction(f);
            arguments.add(argWeight);
        }


        f.setName(name);
        f.setArgumentConcepts(arguments);
        f.setProductConcept(product);
        f.setThreshold(threshold);
        switch (strategy.toLowerCase()){
            case "conjunctive":
                f.setStrategy(Strategy.CONJUNCTIVE);
                break;
            default:
                f.setStrategy(Strategy.WEIGHTED);
                break;
        }

        orderAccess.addAssociativeFunction(f);
    }

}
