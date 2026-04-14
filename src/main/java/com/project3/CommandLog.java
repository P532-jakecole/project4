package com.project3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.project3.Command.Command;
import com.project3.DataTypes.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;

@Repository
@Transactional
public class CommandLog {

    @PersistenceContext
    private EntityManager entityManager;

    public void addCommandLog(CommandType type, String payload, Date timestamp, String user) {
        CommandLogEntry entry = new CommandLogEntry();
        entry.setCommandType(type);
        entry.setPayload(payload);
        entry.setExecutedAt(timestamp);
        entry.setUser(user);

        entityManager.persist(entry);
    }

    public ArrayList<CommandLogEntry> getCommandLog() {
        return (ArrayList<CommandLogEntry>) entityManager
                .createQuery("from CommandLogEntry ", CommandLogEntry.class)
                .getResultList();
    }

    public void addAuditLogString(String event, Integer observationId, Integer patientId, Date timestamp) {
        AuditLogEntry entry = new AuditLogEntry();
        entry.setEvent(event);
        entry.setObservationId(observationId);
        entry.setPatientId(patientId);
        entry.setTimestamp(timestamp);

        entityManager.persist(entry);
    }

    public ArrayList<AuditLogEntry> getAuditLog() {
        return (ArrayList<AuditLogEntry>) entityManager
                .createQuery("from AuditLogEntry ", AuditLogEntry.class)
                .getResultList();
    }
}
