package com.project4.Managers;

import com.project4.AssetLedgerEntryGenerator;
import com.project4.ConsumableLedgerEntryGenerator;
import com.project4.Resources.*;
import com.project4.Repositories.ResourceAccess;
import com.project4.ReversalLedgerEntryGenerator;
import com.project4.State.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class ActionManager {

    private final ResourceAccess resourceAccess;
    private final ConsumableLedgerEntryGenerator ledgerGenerator;
    private final AssetLedgerEntryGenerator assetLedgerEntryGenerator;
    private final ActionStateMachine stateMachine;
    private final ReversalLedgerEntryGenerator reversalLedgerEntryGenerator;

    public ActionManager(ResourceAccess resourceAccess,
                         ConsumableLedgerEntryGenerator ledgerGenerator, AbandonedState abandonedState, CompletedState completedState, InProgressState inProgressState, ProposedState proposedState, SuspendedState suspendedState, AssetLedgerEntryGenerator assetLedgerEntryGenerator, ReversalLedgerEntryGenerator reversalLedgerEntryGenerator, PendingApprovalState pendingApprovalState, ReopenedState reopenedState) {
        this.resourceAccess = resourceAccess;
        this.ledgerGenerator = ledgerGenerator;
        this.assetLedgerEntryGenerator = assetLedgerEntryGenerator;

        List<ActionState> list = new ArrayList<>();
        list.add(abandonedState);
        list.add(completedState);
        list.add(inProgressState);
        list.add(proposedState);
        list.add(suspendedState);
        list.add(pendingApprovalState);
        list.add(reopenedState);

        this.stateMachine = new ActionStateMachine(resourceAccess, this, list);
        this.reversalLedgerEntryGenerator = reversalLedgerEntryGenerator;
    }

    public void implementAction(Integer actionId, String party, String location) throws IllegalStateTransitionException {
        stateMachine.implement(actionId, party, location);
    }

    public void completeAction(Integer actionId) throws IllegalStateTransitionException {
        stateMachine.complete(actionId);
    }

    public void suspendAction(Integer actionId, String reason) throws IllegalStateTransitionException {
        stateMachine.suspend(actionId, reason);
    }

    public void resumeAction(Integer actionId) throws IllegalStateTransitionException {
        stateMachine.resume(actionId);
    }

    public void abandonAction(Integer actionId) throws IllegalStateTransitionException {
        stateMachine.abandon(actionId);
    }

    public void approveAction(Integer actionId, String party, String location) throws IllegalStateTransitionException {
        stateMachine.approve(actionId, party, location);
    }

    public void rejectAction(Integer actionId) throws IllegalStateTransitionException {
        stateMachine.reject(actionId);
    }

    public void reopenAction(Integer actionId) throws IllegalStateTransitionException {
        stateMachine.reopen(actionId);
    }

    public void submitForApprovalAction(Integer actionId) throws IllegalStateTransitionException {
        stateMachine.submitForApproval(actionId);
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
        System.out.println("Generating entries");
        ledgerGenerator.generateEntries(action);
        assetLedgerEntryGenerator.generateEntries(action);
    }

    public void generateReverseLedgerEntries(ImplementedAction action){
        reversalLedgerEntryGenerator.generateEntries(action);
    }

    public ImplementedAction getImplementedAction(Integer proposedId){
        return resourceAccess.getImplementedByProposed(proposedId);
    }

    public void attachResourceAllocation(Integer actionId, Map<String, Object> inputs) throws ParseException {
        ProposedAction action = resourceAccess.getProposedAction(actionId);
        String kind = inputs.get("kind").toString();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date start = null;
        if(inputs.get("start") != null && !Objects.equals(inputs.get("start"), "")){
            start = formatter.parse(inputs.get("start").toString());
        }
        Date end = null;
        if(inputs.get("end") != null && !Objects.equals(inputs.get("end"), "")) {
            end = formatter.parse(inputs.get("end").toString());
        }
        Double quantity = null;
        if(inputs.get("quantity") != null){
            quantity = Double.parseDouble(inputs.get("quantity").toString());
        }

        Integer resourceTypeId = Integer.parseInt(inputs.get("resourceTypeId").toString());
        Integer assetId = null;
        if(inputs.get("assetId") != null){
            assetId = Integer.parseInt(inputs.get("assetId").toString());
        }

        resourceAccess.createResourceAllocation(action, kind, start, end, quantity, resourceTypeId, assetId);
    }
}
