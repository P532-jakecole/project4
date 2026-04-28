package com.project4.Resources;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ProtocolStep {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JsonBackReference("protocol-steps")
    private Protocol protocol;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    @ManyToOne
    @JsonIgnore
    private Protocol subProtocol;

    @ManyToMany
    @JoinTable(
            name = "protocol_step_dependencies",
            joinColumns = @JoinColumn(name = "step_id"),
            inverseJoinColumns = @JoinColumn(name = "depends_on_step_id")
    )
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private List<ProtocolStep> dependsOn = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Protocol getSubProtocol() {
        return subProtocol;
    }

    public void setSubProtocol(Protocol subProtocol) {
        this.subProtocol = subProtocol;
    }

    public List<ProtocolStep> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<ProtocolStep> dependsOn) {
        this.dependsOn = dependsOn;
    }
}
