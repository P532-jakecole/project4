package com.project3.Command;

import com.project3.DataTypes.*;
import com.project3.Factory.ObservationFactory;
import com.project3.OrderAccess;

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
        patientId = (Integer) inputs[0];
        this.observationFactory = observationFactory;
        this.orderAccess = orderAccess;
        this.staff = staff;
        this.inputs = inputs;
        this.type = type;
        this.timestamp = new Date();
    }
    @Override
    public void execute() {
        if(Objects.equals(type, "measurement")){
            Measurement observation = observationFactory.createMeasurement(patientId, (Integer) inputs[1], (Double) inputs[2], (String) inputs[3], (Integer) inputs[4], (Date) inputs[5]);
            if(observation != null){
                orderAccess.addMeasurement(observation);
                observationId = observation.getId();
            }
        }else{
            CategoryObservation observation = observationFactory.createCategoryObservation((Integer) inputs[0], (Integer) inputs[1], (Presence) inputs[2], (Integer) inputs[3], (Date) inputs[4]);
            if(observation != null){
                orderAccess.addCategoryObservation(observation);
                observationId = observation.getId();
            }
        }
    }

    @Override
    public void undo() {

    }
}
