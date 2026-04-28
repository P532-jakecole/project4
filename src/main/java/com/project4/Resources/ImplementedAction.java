package com.project4.Resources;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class ImplementedAction {

    @Id
    @GeneratedValue
    private Integer id;

    public Integer getId() {
        return id;
    }

    public ProposedAction getProposedAction() {
        return proposedAction;
    }

    public void setProposedAction(ProposedAction proposedAction) {
        this.proposedAction = proposedAction;
    }

    public Date getActualStart() {
        return actualStart;
    }

    public void setActualStart(Date actualStart) {
        this.actualStart = actualStart;
    }

    public String getActualParty() {
        return actualParty;
    }

    public void setActualParty(String actualParty) {
        this.actualParty = actualParty;
    }

    public ActionStatus getStatus() {
        return status;
    }

    public void setStatus(ActionStatus status) {
        this.status = status;
    }

    public String getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(String actualLocation) {
        this.actualLocation = actualLocation;
    }

    @OneToOne
    private ProposedAction proposedAction;

    private Date actualStart;

    private String actualParty;
    private String actualLocation;

    @Enumerated(EnumType.STRING)
    private ActionStatus status;
}
