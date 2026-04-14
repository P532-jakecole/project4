package com.project3.DataTypes;

import jakarta.persistence.*;

@Entity
public class AssociativeFunction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String[] argumentConcepts;

    private String productConcept;

    public Integer getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String[] getArgumentConcepts() { return argumentConcepts; }
    public void setArgumentConcepts(String[] argumentConcepts) {
        this.argumentConcepts = argumentConcepts;
    }

    public String getProductConcept() { return productConcept; }
    public void setProductConcept(String productConcept) {
        this.productConcept = productConcept;
    }
}
