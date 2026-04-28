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
}
