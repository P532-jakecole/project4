package com.project3.Managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project3.Command.CreatePatientCommand;
import com.project3.CommandLog;
import com.project3.DataTypes.*;
import com.project3.OrderAccess;
import com.project3.Strategy.DiagnosisEngine;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PatientManager {
    private final OrderAccess orderAccess;
    private final DiagnosisEngine diagnosisEngine;
    private final CommandLog commandLog;
    private ApplicationEventPublisher publisher;
    private final ObjectMapper mapper;

    public PatientManager(OrderAccess orderAccess, ApplicationEventPublisher publisher, CommandLog commandLog, DiagnosisEngine diagnosisEngine) {
        this.orderAccess = orderAccess;
        this.diagnosisEngine = diagnosisEngine;
        this.commandLog = commandLog;
        this.mapper = new ObjectMapper();
    }

    public ArrayList<Patient> getPatients(){
        return orderAccess.findAllPatients();
    }

    public ArrayList<User> getUsers(){
        return orderAccess.getUsers();
    }

    public void addUser(Object[] input){
        User u = new User();
        u.setUsername((String) input[0]);
        u.setRole(Role.valueOf(input[1].toString()));

        orderAccess.addUser(u);
    }

    public ArrayList<Observation> getObservations(Integer id){
        return orderAccess.getObservationsByPatient(id);
    }

    // inputs: [ String fullName, Date dob, String note, String staff ]
    public int createPatients(Object[] inputs) throws JsonProcessingException {
        String staff = inputs[3].toString();
        CreatePatientCommand createPatient = new CreatePatientCommand(inputs, orderAccess, staff);
        createPatient.execute();
        int commandId = commandLog.addCommandLog(CommandType.CreatePatient, mapper.writeValueAsString(createPatient), createPatient.timestamp, createPatient.staff);
        return commandId;
    }

    public ArrayList<Object[]> evaluatePatient(Integer id, String staff){
        List<AssociativeFunction> functions = orderAccess.findAllAssociativeFunctions();
        ArrayList<Observation> observations = orderAccess.getObservationsByPatient(id);

        ArrayList<Object[]> inferredObservations = new ArrayList<>();
        for(AssociativeFunction f : functions){
            List<Observation> evaluationList = diagnosisEngine.evaluate(f, observations);
            if(evaluationList != null){
                List<String> evaluationNames = new ArrayList<>();
                for(Observation obs : evaluationList){
                    if(obs instanceof CategoryObservation){
                        evaluationNames.add(((CategoryObservation) obs).getPhenomenon().getName());
                    }else if(obs instanceof Measurement){
                        evaluationNames.add(((Measurement) obs).getPhenomenonType().getName());
                    }
                }
                inferredObservations.add(new Object[] { f.getProductConcept(), f.getStrategy(), evaluationNames});
            }
        }
        return inferredObservations;
    }
}
