package com.project4.State;

import com.project4.Resources.ImplementedAction;
import com.project4.Resources.ProposedAction;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ReopenedState implements ActionState {

    private final AbandonedState abandonedState;
    private final CompletedState completedState;

    public ReopenedState(AbandonedState abandonedState, CompletedState completedState) {
        this.abandonedState = abandonedState;
        this.completedState = completedState;
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
    public void resume(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void complete(ActionContext ctx){
        ProposedAction action = ctx.getAction();

        ImplementedAction implemented =
                ctx.getResourceAccess().getImplementedByProposed(action.getId());

        implemented.setActualStart(new Date());

        ctx.setImplementedAction(implemented);

        //ctx.getResourceAccess().saveImplementedAction(implemented);
        ctx.getActionManager().generateLedgerEntries(implemented);

        ctx.setState(completedState);
    }

    @Override
    public void abandon(ActionContext ctx){
        ctx.setState(abandonedState);
    }

    @Override
    public void submitForApproval(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
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
        return "REOPENED";
    }
}
