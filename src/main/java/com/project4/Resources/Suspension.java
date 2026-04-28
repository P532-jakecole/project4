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

    public Integer getId() {
        return id;
    }

    public ProposedAction getProposedAction() {
        return proposedAction;
    }

    public void setProposedAction(ProposedAction proposedAction) {
        this.proposedAction = proposedAction;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}