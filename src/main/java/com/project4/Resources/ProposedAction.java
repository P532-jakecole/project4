package com.project4.Resources;

import com.project4.State.ActionState;
import com.project4.PlanNodeVisitor;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class ProposedAction extends PlanNode {

    @ManyToOne
    private Protocol protocol;

    private String party;
    private String location;

    public Date getTimeRef() {
        return timeRef;
    }

    public void setTimeRef(Date timeRef) {
        this.timeRef = timeRef;
    }

    private Date timeRef;

    @Enumerated(EnumType.STRING)
    private ActionStatus status;

    @Transient
    private ActionState state;

//    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ResourceAllocation> allocations = new ArrayList<>();

    @Override
    public ActionStatus getStatus() {
        return status;
    }

    @Override
    public double getTotalAllocatedQuantity(ResourceType type) {
        return 0;
    }

    @Override
    public void accept(PlanNodeVisitor visitor) {
        visitor.visit(this);
    }

//    public List<ResourceAllocation> getAllocations() {
//        return allocations;
//    }
//
//    public void addAllocation(ResourceAllocation allocation) {
//        allocations.add(allocation);
//        allocation.setAction(this);
//    }

    public void setStatus(ActionStatus status) {
        this.status = status;
    }

    public ActionState getState() {
        return state;
    }

    public void setState(ActionState state) {
        this.state = state;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
}
