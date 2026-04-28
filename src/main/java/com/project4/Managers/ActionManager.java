package com.project4.Managers;

import com.project4.AbstractLedgerEntryGenerator;
import com.project4.ActionContext;
import com.project4.ConsumableLedgerEntryGenerator;
import com.project4.Resources.*;
import com.project4.Repositories.ResourceAccess;
import com.project4.State.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ExtractingResponseErrorHandler;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActionManager {

    private final ResourceAccess resourceAccess;
    private final ConsumableLedgerEntryGenerator ledgerGenerator;
    private final ActionStateMachine stateMachine;

    public ActionManager(ResourceAccess resourceAccess,
                         ConsumableLedgerEntryGenerator ledgerGenerator, AbandonedState abandonedState, CompletedState completedState, InProgressState inProgressState, ProposedState proposedState, SuspendedState suspendedState) {
        this.resourceAccess = resourceAccess;
        this.ledgerGenerator = ledgerGenerator;

        List<ActionState> list = new ArrayList<>();
        list.add(abandonedState);
        list.add(completedState);
        list.add(inProgressState);
        list.add(proposedState);
        list.add(suspendedState);
        this.stateMachine = new ActionStateMachine(resourceAccess, this, list);
    }

    public void implementAction(Integer actionId, String party, String location) throws IllegalStateTransitionException {
        stateMachine.implement(actionId, party, location);

//        ProposedAction action = resourceAccess.getProposedAction(actionId);
//
//        ActionContext ctx = new ActionContext(action, this, resourceAccess);
//
//        action.getState().implement(ctx);
//
//        resourceAccess.saveProposedAction(action);
    }

    public void completeAction(Integer actionId) throws IllegalStateTransitionException {
        stateMachine.complete(actionId);
        //        ProposedAction action = resourceAccess.getProposedAction(actionId);
//
//        ActionContext ctx = new ActionContext(action, this, resourceAccess);
//
//        action.getState().complete(ctx);
//
//        resourceAccess.saveProposedAction(action);
    }

    public void suspendAction(Integer actionId, String reason) throws IllegalStateTransitionException {
        stateMachine.suspend(actionId, reason);
//        ProposedAction action = resourceAccess.getProposedAction(actionId);
//
//        ActionContext ctx = new ActionContext(action, this, resourceAccess);
//
//        action.getState().suspend(ctx, reason);
//
//        resourceAccess.saveProposedAction(action);
    }

    public void resumeAction(Integer actionId) throws IllegalStateTransitionException {
        stateMachine.resume(actionId);
//        ProposedAction action = resourceAccess.getProposedAction(actionId);
//
//        ActionContext ctx = new ActionContext(action, this, resourceAccess);
//
//        action.getState().resume(ctx);
//
//        resourceAccess.saveProposedAction(action);
    }

    public void abandonAction(Integer actionId) throws IllegalStateTransitionException {
        stateMachine.abandon(actionId);

//        ProposedAction action = resourceAccess.getProposedAction(actionId);
//
//        ActionContext ctx = new ActionContext(action, this, resourceAccess);
//
//        action.getState().abandon(ctx);
//
//        resourceAccess.saveProposedAction(action);
    }


    public void generateLedgerEntries(ImplementedAction action) {
        ledgerGenerator.generateEntries(action);
    }

    public void attachResourceAllocation(Integer actionId, ResourceAllocation allocation) {
        ProposedAction action = resourceAccess.getProposedAction(actionId);
        allocation.setAction(action);
        resourceAccess.saveResourceAllocation(allocation);
    }
}
