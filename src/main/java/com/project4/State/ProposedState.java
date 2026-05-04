package com.project4.State;

import com.project4.Resources.ImplementedAction;
import com.project4.Resources.ProposedAction;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ProposedState implements ActionState {
    private final AbandonedState abandonedState;
    private final SuspendedState suspendedState;
    private final PendingApprovalState pendingApprovalState;

    public ProposedState(AbandonedState abandonedState, @Lazy SuspendedState suspendedState, @Lazy PendingApprovalState pendingApprovalState) {
        this.abandonedState = abandonedState;
        this.suspendedState = suspendedState;
        this.pendingApprovalState = pendingApprovalState;
    }

    @Override
    public void implement(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        ctx.setState(suspendedState);
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
    public void abandon(ActionContext ctx) {
        ctx.setState(abandonedState);
    }

    @Override
    public void submitForApproval(ActionContext ctx){
        ctx.setState(pendingApprovalState);
    }

    @Override
    public void approve(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void reject(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void reopen(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public String name() {
        return "PROPOSED";
    }
}
