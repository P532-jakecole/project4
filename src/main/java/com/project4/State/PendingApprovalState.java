package com.project4.State;

import com.project4.Resources.ImplementedAction;
import com.project4.Resources.ProposedAction;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PendingApprovalState implements ActionState {

    private final ProposedState proposedState;
    private final InProgressState inProgressState;

    public PendingApprovalState(ProposedState proposedState, InProgressState inProgressState) {
        this.proposedState = proposedState;
        this.inProgressState = inProgressState;
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
    public void complete(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void abandon(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void submitForApproval(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void approve(ActionContext ctx){
        ProposedAction action = ctx.getAction();

        ImplementedAction implemented = new ImplementedAction();
        implemented.setProposedAction(action);
        implemented.setActualStart(new Date());
        implemented.setActualParty(ctx.getActualParty());
        implemented.setActualLocation(ctx.getActualLocation());

        ctx.setImplementedAction(implemented);

        //ctx.getResourceAccess().saveImplementedAction(implemented);

        ctx.setState(inProgressState);
    }

    @Override
    public void reject(ActionContext ctx){
        ctx.setState(proposedState);
    }

    @Override
    public void reopen(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public String name() {
        return "PENDING_APPROVAL";
    }
}
