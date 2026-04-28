package com.project4.Resources;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
public class Account {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @Enumerated(EnumType.STRING)
    private AccountKind kind;

    @ManyToOne
    @JoinColumn(name = "resource_type_id", nullable = true)
    @JsonManagedReference
    private ResourceType resourceType;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    private double amount;

//    @ManyToOne
//    private ProposedAction action;
//
//    public ProposedAction getAction() {
//        return action;
//    }
//
//    public void setAction(ProposedAction action) {
//        this.action = action;
//    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountKind getKind() {
        return kind;
    }

    public void setKind(AccountKind kind) {
        this.kind = kind;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }
}
