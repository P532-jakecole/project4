package com.project4.Resources;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Transaction {

    @Id
    @GeneratedValue
    private Integer id;

    private String description;

    private Date createdAt;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<Entry> entries;

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}
