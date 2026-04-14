package com.project3.DataTypes;

import jakarta.persistence.*;

@Entity
public class Measurement extends Observation {

    @ManyToOne
    private PhenomenonType phenomenonType;

    private Double amount;

    private String unit;

    public PhenomenonType getPhenomenonType() { return phenomenonType; }
    public void setPhenomenonType(PhenomenonType phenomenonType) {
        this.phenomenonType = phenomenonType;
    }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}