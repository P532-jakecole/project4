package com.project3.DataTypes;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.project3.CommandLog;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class CommandLogEntry {

    @Id
    @GeneratedValue
    private Integer id;

    private CommandType commandType;

    @Lob
    private String payload;

    @Temporal(TemporalType.TIMESTAMP)
    private Date executedAt;

    private String user;

    public Integer getId() {
        return id;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Date getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Date executedAt) {
        this.executedAt = executedAt;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
