package com.project4.State;

import com.project4.Resources.ImplementedAction;
import com.project4.Resources.ProposedAction;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ProposedState implements ActionState {
    private final InProgressState inProgressState;
    private final AbandonedState abandonedState;
    private final SuspendedState suspendedState;

    public ProposedState(InProgressState inProgressState, AbandonedState abandonedState, @Lazy SuspendedState suspendedState) {
        this.inProgressState = inProgressState;
        this.abandonedState = abandonedState;
        this.suspendedState = suspendedState;
    }

    @Override
    public void implement(ActionContext ctx) {
        ProposedAction action = ctx.getAction();

        ImplementedAction implemented = new ImplementedAction();
        implemented.setProposedAction(action);
        implemented.setActualStart(new Date());

        ctx.getResourceAccess().saveImplementedAction(implemented);

        ctx.setState(inProgressState);
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
    public String name() {
        return "PROPOSED";
    }
}
