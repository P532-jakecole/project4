package com.project4.Managers;

import com.project4.ConsumableLedgerEntryGenerator;
import com.project4.Resources.*;
import com.project4.Repositories.ResourceAccess;
import com.project4.State.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public ProposedAction getAction(Integer id){
        return resourceAccess.getProposedAction(id);
    }

    public List<ProposedAction> getActions(){
        return resourceAccess.getProposedActions();
    }

    public List<ResourceAllocation> getResourceAllocations(Integer id){
        return resourceAccess.getResourceAllocations(id);
    }

    public void generateLedgerEntries(ImplementedAction action) {
        ledgerGenerator.generateEntries(action);
    }

    public void attachResourceAllocation(Integer actionId, Map<String, Object> inputs) {
        ProposedAction action = resourceAccess.getProposedAction(actionId);
        String kind = inputs.get("kind").toString();
        LocalDate startDate = LocalDate.parse(inputs.get("start").toString());
        Date start = java.sql.Date.valueOf(startDate);
        LocalDate endDate = LocalDate.parse(inputs.get("end").toString());
        Date end = java.sql.Date.valueOf(endDate);
        Double quantity = Double.parseDouble(inputs.get("quantity").toString());
        Integer resourceTypeId = Integer.parseInt(inputs.get("resourceTypeId").toString());
        Integer assetId = null;
        if(inputs.get("assetId") != null){
            assetId = Integer.parseInt(inputs.get("assetId").toString());
        }

        resourceAccess.createResourceAllocation(action, kind, start, end, quantity, resourceTypeId, assetId);
    }
}
