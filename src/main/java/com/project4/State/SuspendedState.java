package com.project4.State;

import org.springframework.stereotype.Component;

@Component
public class SuspendedState implements ActionState {

    private final AbandonedState abandonedState;
    private final ProposedState proposedState;

    public SuspendedState(AbandonedState abandonedState, ProposedState proposedState) {
        this.abandonedState = abandonedState;
        this.proposedState = proposedState;
    }

    @Override
    public void implement(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void suspend(ActionContext ctx, String reason) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void resume(ActionContext ctx) {
        ctx.setState(proposedState);
    }

    @Override
    public void complete(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void abandon(ActionContext ctx) {
        ctx.setState(abandonedState);
    }

    @Override
    public String name() {
        return "SUSPENDED";
    }
}
