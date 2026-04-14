package com.project3.DataTypes;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class AuditLogEntry {

    @Id
    @GeneratedValue
    private Integer id;

    private String event;

    private Integer observationId;

    private Integer patientId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public Integer getId() {
        return id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Integer getObservationId() {
        return observationId;
    }

    public void setObservationId(Integer observationId) {
        this.observationId = observationId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
