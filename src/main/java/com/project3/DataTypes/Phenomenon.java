package com.project3.DataTypes;

import jakarta.persistence.*;

@Entity
public class Phenomenon {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @ManyToOne
    private PhenomenonType phenomenonType;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PhenomenonType getPhenomenonType() {
        return phenomenonType;
    }

    public void setPhenomenonType(PhenomenonType phenomenonType) {
        this.phenomenonType = phenomenonType;
    }
}
