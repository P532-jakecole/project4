package com.project4.State;

import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.ImplementedAction;
import com.project4.Resources.ProposedAction;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class InProgressState implements ActionState {

    private final SuspendedState suspendedState;
    private final AbandonedState abandonedState;
    private final CompletedState completedState;

    public InProgressState(@Lazy SuspendedState suspendedState, AbandonedState abandonedState, CompletedState completedState, ResourceAccess resourceAccess) {
        this.suspendedState = suspendedState;
        this.abandonedState = abandonedState;
        this.completedState = completedState;
    }

    @Override
    public void complete(ActionContext ctx) {
        ProposedAction action = ctx.getAction();

        ImplementedAction implemented =
                ctx.getResourceAccess().getImplementedByProposed(action.getId());

        implemented.setActualStart(new Date());

        ctx.getResourceAccess().saveImplementedAction(implemented);
        ctx.getActionManager().generateLedgerEntries(implemented);

        ctx.setState(completedState);
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        ctx.getResourceAccess().createSuspension(ctx.getAction(), reason);
        ctx.setState(suspendedState);
    }

    @Override
    public void implement(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void resume(ActionContext ctx) throws IllegalStateTransitionException {
        throw new IllegalStateTransitionException();
    }

    @Override
    public void abandon(ActionContext ctx) {
        ctx.setState(abandonedState);
    }

    @Override
    public String name() {
        return "IN_PROGRESS";
    }
}
