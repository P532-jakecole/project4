package com.project4.State;

public interface ActionState {
    void implement(ActionContext ctx) throws IllegalStateTransitionException;
    void suspend(ActionContext ctx, String reason) throws IllegalStateTransitionException;
    void resume(ActionContext ctx) throws IllegalStateTransitionException;
    void complete(ActionContext ctx) throws IllegalStateTransitionException;
    void abandon(ActionContext ctx) throws IllegalStateTransitionException;
    String name();
}
