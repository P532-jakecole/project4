package com.project3.DataTypes;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Observation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Patient patient;

    @Temporal(TemporalType.TIMESTAMP)
    private Date recordingTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date applicabilityTime;

    @ManyToOne
    private Protocol protocol;

    @Enumerated(EnumType.STRING)
    private ObservationStatus status;

    public Integer getId() { return id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Date getRecordingTime() { return recordingTime; }
    public void setRecordingTime(Date recordingTime) { this.recordingTime = recordingTime; }

    public Date getApplicabilityTime() { return applicabilityTime; }
    public void setApplicabilityTime(Date applicabilityTime) {
        this.applicabilityTime = applicabilityTime;
    }

    public Protocol getProtocol() { return protocol; }
    public void setProtocol(Protocol protocol) { this.protocol = protocol; }

    public ObservationStatus getStatus() { return status; }
    public void setStatus(ObservationStatus status) { this.status = status; }
}
