package com.project4.Resources;

import com.project4.PlanNodeVisitor;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PlanNode {

    @Id
    @GeneratedValue
    protected Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    protected Plan parent;

    public Integer getId() { return id; }

    public Plan getParent() { return parent; }
    public void setParent(Plan parent) { this.parent = parent; }

    public abstract ActionStatus getStatus();

    public abstract double getTotalAllocatedQuantity(ResourceType type);

    public abstract void accept(PlanNodeVisitor visitor);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}