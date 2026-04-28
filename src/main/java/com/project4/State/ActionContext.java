package com.project4.State;

import com.project4.Managers.ActionManager;
import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.ActionStatus;
import com.project4.Resources.ImplementedAction;
import com.project4.Resources.ProposedAction;

public class ActionContext {

    private final ProposedAction action;
    private final ActionManager actionManager;
    private final ResourceAccess resourceAccess;

    public ImplementedAction getImplementedAction() {
        return implementedAction;
    }

    public void setImplementedAction(ImplementedAction implementedAction) {
        this.implementedAction = implementedAction;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getActualParty() {
        return actualParty;
    }

    public void setActualParty(String actualParty) {
        this.actualParty = actualParty;
    }

    public String getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(String actualLocation) {
        this.actualLocation = actualLocation;
    }

    private ImplementedAction implementedAction;

    // transient transition data
    private String reason;
    private String actualParty;
    private String actualLocation;

    public ActionContext(ProposedAction action,
                         ActionManager actionManager,
                         ResourceAccess resourceAccess) {
        this.action = action;
        this.actionManager = actionManager;
        this.resourceAccess = resourceAccess;
    }

    public ProposedAction getAction() {
        return action;
    }

    public void setState(ActionState state) {
        action.setState(state);

        switch(state.name().toLowerCase()){
            case "proposed":
                action.setStatus(ActionStatus.PROPOSED);
                break;
            case "suspended":
                action.setStatus(ActionStatus.SUSPENDED);
                break;
            case "in_progress":
                action.setStatus(ActionStatus.IN_PROGRESS);
                break;
            case "completed":
                action.setStatus(ActionStatus.COMPLETED);
                break;
            case "abandoned":
                action.setStatus(ActionStatus.ABANDONED);
                break;
        }

    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public ResourceAccess getResourceAccess() {
        return resourceAccess;
    }
}
