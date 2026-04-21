package com.project3.Decorator;

import com.project3.DataTypes.Presence;
import com.project3.OrderAccess;

import java.util.Date;

public class ObservationRequest {
    private int patientId;
    private String type;
    private Integer protocol;
    private final OrderAccess orderAccess;
    private Date applicableTime;

    private int phenomenonTypeId = -1;
    private int phenomenonId = -1;
    private String unit = null;
    private Double amount = null;
    private Presence presence = null;
    private Date recordingTimestamp = null;
    private String user = null;
    private String anomaly = null;


    public ObservationRequest(int patientId, Integer protocol, String type, OrderAccess orderAccess, Date applicableTime) {
        this.patientId = patientId;
        this.protocol = protocol;
        this.type = type;
        this.orderAccess = orderAccess;
        this.applicableTime = applicableTime;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getType() {
        return type;
    }

    public Integer getProtocol() {
        return protocol;
    }

    public int getPhenomenonTypeId() {
        return phenomenonTypeId;
    }

    public void setPhenomenonTypeId(int phenomenonTypeId) {
        this.phenomenonTypeId = phenomenonTypeId;
    }

    public int getPhenomenonId() {
        return phenomenonId;
    }

    public void setPhenomenonId(int phenomenonId) {
        this.phenomenonId = phenomenonId;
    }

    public Presence getPresence() {
        return presence;
    }

    public void setPresence(Presence presence) {
        this.presence = presence;
    }

    public Date getRecordingTimestamp() {
        return recordingTimestamp;
    }

    public void setRecordingTimestamp(Date recordingTimestamp) {
        this.recordingTimestamp = recordingTimestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public OrderAccess getOrderAccess() {
        return orderAccess;
    }

    public String getAnomaly() {
        return anomaly;
    }

    public void setAnomaly(String anomaly) {
        this.anomaly = anomaly;
    }

    public Date getApplicableTime() {
        return applicableTime;
    }

    public void setApplicableTime(Date applicableTime) {
        this.applicableTime = applicableTime;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
