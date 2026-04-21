package com.project3.DataTypes;
import jakarta.persistence.*;


@Entity
public class ArgumentWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String concept;

    private Double weight;

    @ManyToOne
    private AssociativeFunction function;

    public String getConcept() {
        return concept;
    }
    public void setConcept(String concept) {
        this.concept = concept;
    }
    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    public AssociativeFunction getFunction() {
        return function;
    }
    public void setFunction(AssociativeFunction function) {
        this.function = function;
    }
    public Integer getId() {
        return id;
    }
}
