package com.project3.DataTypes;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class PhenomenonType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Enumerated(EnumType.STRING)
    private PhenomenonKind kind;

    @ElementCollection
    private List<String> allowedUnits;

    @JsonManagedReference
    @OneToMany(mappedBy = "phenomenonType")
    private List<Phenomenon> phenomena = new ArrayList<>();

    private Double normalMin;
    private Double normalMax;

    public Integer getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PhenomenonKind getKind() { return kind; }
    public void setKind(PhenomenonKind kind) { this.kind = kind; }

    public List<String> getAllowedUnits() { return allowedUnits; }
    public void setAllowedUnits(List<String> allowedUnits) { this.allowedUnits = allowedUnits; }

    public List<Phenomenon> getPhenomena() { return phenomena; }
    public void setPhenomena(List<Phenomenon> phenomena) { this.phenomena = phenomena; }
    public void addPhenomenon(Phenomenon p) {
        if (phenomena == null) {
            phenomena = new ArrayList<>();
        }
        phenomena.add(p);
        p.setPhenomenonType(this);
    }

    public Double getNormalMin() { return normalMin; }
    public void setNormalMin(Double normalMin) { this.normalMin = normalMin; }

    public Double getNormalMax() { return normalMax; }
    public void setNormalMax(Double normalMax) { this.normalMax = normalMax; }
}
