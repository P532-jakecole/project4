package com.project3.DataTypes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
public class Phenomenon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Integer id;

    private String name;

    @JsonIgnoreProperties("phenomena")
    @ManyToOne
    private PhenomenonType phenomenonType;

    @ManyToOne
    @JsonIgnoreProperties("children")
    @JoinColumn(name = "parent_id")
    private Phenomenon parentConcept;

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

    public Phenomenon getParentConcept() {
        return parentConcept;
    }

    public void setParentConcept(Phenomenon parentConcept) {
        this.parentConcept = parentConcept;
    }

}
