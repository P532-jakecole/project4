package com.project4.State;

import com.project4.Managers.ActionManager;
import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActionStateMachine {

    private final ResourceAccess resourceAccess;
    private final ActionManager actionManager;

    private final Map<String, ActionState> states = new HashMap<String, ActionState>();

    public ActionStateMachine(ResourceAccess resourceAccess,
                              ActionManager actionManager,
                              List<ActionState> states) {
        this.resourceAccess = resourceAccess;
        this.actionManager = actionManager;
        for(ActionState state : states) {
            this.states.put(state.name(), state);
        }
    }


    public void implement(Integer actionId, String party, String location) throws IllegalStateTransitionException {

        ProposedAction action = resourceAccess.getProposedAction(actionId);

        ActionContext ctx = new ActionContext(action, actionManager, resourceAccess);
        ctx.setActualParty(party);
        ctx.setActualLocation(location);

        getState(action).implement(ctx);

        persist(ctx);
    }

    public void complete(Integer actionId) throws IllegalStateTransitionException {

        ProposedAction action = resourceAccess.getProposedAction(actionId);

        ActionContext ctx = new ActionContext(action, actionManager, resourceAccess);

        getState(action).complete(ctx);

        persist(ctx);
    }

    public void suspend(Integer actionId, String reason) throws IllegalStateTransitionException {

        ProposedAction action = resourceAccess.getProposedAction(actionId);

        ActionContext ctx = new ActionContext(action, actionManager, resourceAccess);
        ctx.setReason(reason);

        getState(action).suspend(ctx, reason);

        persist(ctx);
    }

    public void resume(Integer actionId) throws IllegalStateTransitionException {

        ProposedAction action = resourceAccess.getProposedAction(actionId);

        ActionContext ctx = new ActionContext(action, actionManager, resourceAccess);

        getState(action).resume(ctx);

        persist(ctx);
    }

    public void abandon(Integer actionId) throws IllegalStateTransitionException {

        ProposedAction action = resourceAccess.getProposedAction(actionId);

        ActionContext ctx = new ActionContext(action, actionManager, resourceAccess);

        getState(action).abandon(ctx);

        persist(ctx);
    }


    private ActionState getState(ProposedAction action) {
        String stateName = action.getStatus().name();
        ActionState state = states.get(stateName);

        if (state == null) {
            throw new IllegalStateException("No state bean found for: " + stateName);
        }

        return state;
    }

    private void persist(ActionContext ctx) {
        resourceAccess.saveProposedAction(ctx.getAction());

        if (ctx.getImplementedAction() != null) {
            resourceAccess.saveImplementedAction(ctx.getImplementedAction());
        }
    }
}
