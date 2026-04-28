package com.project4.Resources;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Id;

import java.util.List;

@Entity
public class Protocol {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "protocol", cascade = CascadeType.ALL)
    @JsonManagedReference("protocol-steps")
    private List<ProtocolStep> steps;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProtocolStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ProtocolStep> steps) {
        this.steps = steps;
    }
}
