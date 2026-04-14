package com.project3.DataTypes;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class PhenomenonType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Enumerated(EnumType.STRING)
    private PhenomenonKind kind;

    private String[] allowedUnits;
    @OneToMany(mappedBy = "phenomenonType")
    private List<Phenomenon> phenomena;

    public Integer getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PhenomenonKind getKind() { return kind; }
    public void setKind(PhenomenonKind kind) { this.kind = kind; }

    public String[] getAllowedUnits() { return allowedUnits; }
    public void setAllowedUnits(String[] allowedUnits) { this.allowedUnits = allowedUnits; }

    public List<Phenomenon> getPhenomena() { return phenomena; }
    public void setPhenomena(List<Phenomenon> phenomena) { this.phenomena = phenomena; }
}
