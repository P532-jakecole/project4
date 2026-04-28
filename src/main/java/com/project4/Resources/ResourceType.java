package com.project4.Resources;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class ResourceType {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ResourceKind kind;

    private String unit;

    @ManyToOne
    @JoinColumn(name = "pool_account_id", nullable = true)
    @JsonBackReference
    private Account poolAccount;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceKind getKind() {
        return kind;
    }

    public void setKind(ResourceKind kind) {
        this.kind = kind;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Account getPoolAccount() {
        return poolAccount;
    }

    public void setPoolAccount(Account poolAccount) {
        this.poolAccount = poolAccount;
    }
}
