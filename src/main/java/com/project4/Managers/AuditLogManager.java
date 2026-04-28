package com.project4.Managers;

import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.AuditLogEntry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogManager {
    private final ResourceAccess resourceAccess;

    public AuditLogManager(ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
    }

    public List<AuditLogEntry> getAuditLog() {
        return resourceAccess.getAuditLogs();
    }
}
