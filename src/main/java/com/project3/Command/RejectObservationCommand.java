package com.project3.Command;

import com.project3.DataTypes.Observation;
import com.project3.OrderAccess;

import java.util.Date;

public class RejectObservationCommand implements Command {
    public Integer observationId;
    public String reason;
    private final OrderAccess orderAccess;
    public String staff;
    public Integer patientId = null;
    public Date timestamp;

    public RejectObservationCommand(Integer id, String reason, OrderAccess orderAccess, String staff) {
        this.observationId = id;
        this.reason = reason;
        this.orderAccess = orderAccess;
        this.staff = staff;
        this.timestamp = new Date();
    }

    @Override
    public void execute() {
        Observation observation = orderAccess.rejectObservation(observationId);
        if(observation != null) {
            patientId = observation.getPatient().getId();
        }
    }

    @Override
    public void undo() {
        this.timestamp = new Date();
        orderAccess.setActiveObservationStatus(observationId);
    }
}
