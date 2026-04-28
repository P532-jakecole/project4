package com.project4.Resources;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Suspension {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private ProposedAction proposedAction;

    private String reason;

    private Date startDate;
    private Date endDate;
}