package com.project3.Command;

import com.project3.DataTypes.Patient;
import com.project3.OrderAccess;

import java.time.LocalDate;
import java.util.Date;

public class CreatePatientCommand implements Command {
    public String staff;
    private final OrderAccess orderAccess;
    public String name;
    public Date dob;
    public String note;
    public Date timestamp;

    public Integer patientId;

    public CreatePatientCommand(Object[] inputs, OrderAccess orderAccess, String staff) {
        this.name = (String) inputs[0];
        LocalDate localDate = LocalDate.parse(inputs[1].toString());
        this.dob = java.sql.Date.valueOf(localDate);
        this.note = (String) inputs[2];
        this.orderAccess = orderAccess;
        this.staff = staff;
        this.timestamp = new Date();
    }


    @Override
    public void execute() {
        Patient p = new Patient();
        p.setFullName(name);
        p.setDateOfBirth(dob);
        p.setNote(note);
        orderAccess.addPatient(p);
        patientId = p.getId();
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported");
    }
}
