package com.project4.Resources;

import jakarta.persistence.*;

@Entity
public class ResourceAllocation {

    @Id
    @GeneratedValue
    private Integer id;

//    private Integer actionId;
//    private String actionType;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private ProposedAction action;

    @ManyToOne
    private ResourceType resourceType;

    private Double quantity;

    @Enumerated(EnumType.STRING)
    private AllocationKind kind;

    private String assetId;

    //TODO change to actual time
    private String timePeriod;

    @ManyToOne
    public ProposedAction getAction() {
        return action;
    }

    public Integer getId() {
        return id;
    }

//    public Integer getActionId() {
//        return actionId;
//    }
//
//    public void setActionID(Integer actionId) {
//        this.actionId = actionId;
//    }

    public void setAction(ProposedAction action) {
        this.action = action;
    }

//    public String getActionType() {
//        return actionType;
//    }
//
//    public void setActionType(String actionType) {
//        this.actionType = actionType;
//    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public AllocationKind getKind() {
        return kind;
    }

    public void setKind(AllocationKind kind) {
        this.kind = kind;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }
}
