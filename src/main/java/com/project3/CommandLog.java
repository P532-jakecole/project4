package com.project3;


import com.project3.DataTypes.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class CommandLog {

    @PersistenceContext
    private EntityManager entityManager;

    public int addCommandLog(CommandType type, String payload, Date timestamp, String user, int observationId) {
        CommandLogEntry entry = new CommandLogEntry();
        entry.setCommandType(type);
        entry.setPayload(payload);
        entry.setExecutedAt(timestamp);
        entry.setUser(user);
        entry.setObservationId(observationId);

        entityManager.persist(entry);
        return entry.getId();
    }

    public int addCommandLog(CommandType type, String payload, Date timestamp, String user) {
        CommandLogEntry entry = new CommandLogEntry();
        entry.setCommandType(type);
        entry.setPayload(payload);
        entry.setExecutedAt(timestamp);
        entry.setUser(user);

        entityManager.persist(entry);
        return entry.getId();
    }

    public ArrayList<CommandLogEntry> getCommandLog() {
        return (ArrayList<CommandLogEntry>) entityManager
                .createQuery("from CommandLogEntry ", CommandLogEntry.class)
                .getResultList();
    }

    public void updateCommandLogEntry(CommandLogEntry entry) {
        entityManager.merge(entry);
    }

    public boolean checkAuditLogEntered(int patientId, String concept) {
        List<AuditLogEntry> results = entityManager.createQuery(
                        "FROM AuditLogEntry o " +
                                "WHERE o.patientId = :pid " +
                                "AND o.event LIKE :concept",
                        AuditLogEntry.class
                )
                .setParameter("pid", patientId)
                .setParameter("concept", "%Inference: " + concept + "%")
                .getResultList();

        return !results.isEmpty();
    }

    public CommandLogEntry getCommandLogById(int id) {
        return entityManager.find(CommandLogEntry.class, id);
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
