package com.project3.DataTypes;

import jakarta.persistence.*;

@Entity
public class Protocol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private AccuracyRating accuracyRating;

    public Integer getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AccuracyRating getAccuracyRating() { return accuracyRating; }
    public void setAccuracyRating(AccuracyRating accuracyRating) {
        this.accuracyRating = accuracyRating;
    }
}
