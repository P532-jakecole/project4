package com.project3.Command;

import com.project3.DataTypes.*;
import com.project3.Decorator.*;
import com.project3.Factory.ObservationFactory;
import com.project3.OrderAccess;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class RecordObservationCommand implements Command {
    public Integer patientId;
    private final ObservationFactory observationFactory;
    private final OrderAccess orderAccess;
    public String staff;
    public String type;
    public Object[] inputs;
    public Date timestamp;

    public Integer observationId;

    public RecordObservationCommand(Object[] inputs, ObservationFactory observationFactory, OrderAccess orderAccess,String staff, String type) {
        patientId = Integer.parseInt(inputs[0].toString());
        this.observationFactory = observationFactory;
        this.orderAccess = orderAccess;
        this.staff = staff;
        this.inputs = inputs;
        this.type = type.toLowerCase();
        this.timestamp = new Date();
    }
    @Override
    public void execute() throws Exception {
        ObservationProcessor processor = new AuditStampingDecorator(
                new AnomalyFlaggingDecorator(
                        new UnitValidationDecorator(
                                new BaseObservationProcessor())), staff);




        if(Objects.equals(type, "measurement")){
            LocalDate localDate = LocalDate.parse(inputs[6].toString());
            Integer protocolId = null;
            if(inputs[4] != ""){
                protocolId = Integer.parseInt(inputs[4].toString());
            }
            ObservationRequest request = new ObservationRequest(patientId, protocolId, type, orderAccess, java.sql.Date.valueOf(localDate));
            request.setPhenomenonTypeId(Integer.parseInt(inputs[1].toString()));
            request.setAmount(Double.parseDouble(inputs[2].toString()));
            request.setUnit((String) inputs[3]);

            processor.process(request);

            // patientId, Integer.parseInt(inputs[1].toString()), Double.parseDouble(inputs[2].toString()), (String) inputs[3], protocolId, java.sql.Date.valueOf(localDate)

            Measurement observation = observationFactory.createMeasurement(request);
            if (observation != null) {
                orderAccess.addMeasurement(observation);
                observationId = observation.getId();
            }
        }else{
            LocalDate localDate = LocalDate.parse(inputs[4].toString());
            Presence presence = null;
            if(inputs[2].toString().equalsIgnoreCase("present")){
                presence = Presence.PRESENT;
            }else if(inputs[2].toString().equalsIgnoreCase("absent")){
                presence = Presence.ABSENT;
            }
            Integer protocolId = null;
            if(inputs[3] != ""){
                protocolId = Integer.parseInt(inputs[3].toString());
            }
            ObservationRequest request = new ObservationRequest(patientId, protocolId, type, orderAccess, java.sql.Date.valueOf(localDate));
            request.setPhenomenonId(Integer.parseInt(inputs[1].toString()));
            request.setPresence(presence);
            processor.process(request);

            // Integer.parseInt(inputs[0].toString()), Integer.parseInt(inputs[1].toString()), presence, protocolId, java.sql.Date.valueOf(localDate)

            CategoryObservation observation = observationFactory.createCategoryObservation(request);
            if(observation != null){
                orderAccess.addCategoryObservation(observation);
                observationId = observation.getId();
            }
        }
    }

    @Override
    public void undo() {
        this.timestamp = new Date();
        System.out.println(Arrays.toString(inputs));
        observationId = (Integer) inputs[6];
        RejectObservationCommand rejectCommand = new RejectObservationCommand(observationId, "Undone by user", orderAccess, staff);
        rejectCommand.execute();
    }
}
