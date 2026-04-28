package com.project4.Resources;

import com.project4.PlanNodeVisitor;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Plan extends PlanNode {

    public Date getTargetStartDate() {
        return targetStartDate;
    }

    public void setTargetStartDate(Date targetStartDate) {
        this.targetStartDate = targetStartDate;
    }

    private Date targetStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_protocol_id")
    private Protocol sourceProtocol;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlanNode> children = new ArrayList<>();

    public Protocol getSourceProtocol() {
        return sourceProtocol;
    }

    public void setSourceProtocol(Protocol sourceProtocol) {
        this.sourceProtocol = sourceProtocol;
    }

    @Override
    public ActionStatus getStatus() {
        boolean anyInProgress = false;
        boolean anySuspended = false;
        boolean allCompleted = true;
        boolean allAbandoned = true;

        for (PlanNode child : children) {
            ActionStatus status = child.getStatus();

            if (status == ActionStatus.IN_PROGRESS) anyInProgress = true;
            if (status == ActionStatus.SUSPENDED) anySuspended = true;
            if (status != ActionStatus.COMPLETED) allCompleted = false;
            if (status != ActionStatus.ABANDONED) allAbandoned = false;
        }

        if (allCompleted) return ActionStatus.COMPLETED;
        if (anyInProgress) return ActionStatus.IN_PROGRESS;
        if (anySuspended) return ActionStatus.SUSPENDED;
        if (allAbandoned) return ActionStatus.ABANDONED;

        return ActionStatus.PROPOSED;
    }

    public List<PlanNode> getChildren() {
        return children;
    }

    public void setChildren(List<PlanNode> children) {
        this.children = children;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    @Override
    public double getTotalAllocatedQuantity(ResourceType type) {
        return children.stream()
                .mapToDouble(c -> c.getTotalAllocatedQuantity(type))
                .sum();
    }

    @Override
    public void accept(PlanNodeVisitor visitor) {
        visitor.visit(this);
        children.forEach(child -> child.accept(visitor));
    }
}
