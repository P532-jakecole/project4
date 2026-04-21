package com.project3.DataTypes;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.List;

@Entity
public class AssociativeFunction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArgumentWeight> argumentConcepts;

    private String productConcept;

    @Enumerated(EnumType.STRING)
    private Strategy strategy;

    private Double threshold;

    public Integer getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<ArgumentWeight> getArgumentConcepts() { return argumentConcepts; }
    public void setArgumentConcepts(List<ArgumentWeight> argumentConcepts) { this.argumentConcepts = argumentConcepts; }

    public String getProductConcept() { return productConcept; }
    public void setProductConcept(String productConcept) {
        this.productConcept = productConcept;
    }

    public Strategy getStrategy() { return strategy; }
    public void setStrategy(Strategy strategy) { this.strategy = strategy; }

    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }
}
