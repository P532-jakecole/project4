package com.project4.State;

import org.springframework.stereotype.Component;

@Component
public class CompletedState implements ActionState {
    @Override
    public void implement(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void suspend(ActionContext ctx, String reason) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void resume(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void complete(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void abandon(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public String name() {
        return "COMPLETED";
    }
}
