package com.project4.Resources;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class AuditLogEntry {

    @Id
    @GeneratedValue
    private Integer id;

    private String event;

    private Integer accountId;
    private Integer entryId;
    private Integer actionId;

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

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

    public Integer getActionId() {
        return actionId;
    }

    public void setActionId(Integer actionId) {
        this.actionId = actionId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
