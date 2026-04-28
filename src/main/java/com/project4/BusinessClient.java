package com.project4;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.project4.Resources.*;
import com.project4.Managers.*;
import com.project4.State.IllegalStateTransitionException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(
        origins = "https://p532-jakecole.github.io",
        allowedHeaders = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
@RequestMapping("/api")
public class BusinessClient {

    private final ProtocolManager protocolManager;
    private final ResourceTypeManager resourceTypeManager;
    private final PlanManager planManager;
    private final ActionManager actionManager;
    private final AccountManager accountManager;
    private final AuditLogManager auditLogManager;

    public BusinessClient(
            ProtocolManager protocolManager,
            ResourceTypeManager resourceTypeManager,
            PlanManager planManager,
            ActionManager actionManager,
            AccountManager accountManager,
            AuditLogManager auditLogManager
    ) {
        this.protocolManager = protocolManager;
        this.resourceTypeManager = resourceTypeManager;
        this.planManager = planManager;
        this.actionManager = actionManager;
        this.accountManager = accountManager;
        this.auditLogManager = auditLogManager;
    }

    @GetMapping("/protocols")
    public List<Protocol> getProtocols() {
        return protocolManager.getProtocols();
    }

    @PostMapping("/protocols")
    public void createProtocol(@RequestBody Map<String, Object> protocol) {
        protocolManager.createProtocol(protocol);
    }


    @GetMapping("/resource-types")
    public List<ResourceType> getResourceTypes() {
        return resourceTypeManager.getAllResourceTypes();
    }

    @PostMapping("/resource-types")
    public void createResourceType(@RequestBody Map<String, Object> resourceType) {
        resourceTypeManager.createResourceType(resourceType);
    }


    @PostMapping("/plans")
    public void createPlan(@RequestBody Map<String, Object> plan) {
        planManager.createPlan(plan);
    }

    @GetMapping("/plans/{id}")
    public Plan getPlan(@PathVariable Integer id) {
        return planManager.getPlanTree(id);
    }

    @GetMapping("/plans/{id}/report")
    public List<String> getPlanReport(@PathVariable Integer id) {
        return planManager.generateDepthFirstReport(id);
    }

    @GetMapping("/plans")
    public List<Plan> getPlans() {
        return planManager.getPlans();
    }

    @GetMapping("/actions/{id}")
    public ProposedAction getAction(@PathVariable Integer id){
        return actionManager.getAction(id);
    }

    @GetMapping("/actions")
    public List<ProposedAction> getActions(){
        return actionManager.getActions();
    }

    @PostMapping("/actions/{id}/implement")
    public void implementAction(@PathVariable Integer id, @RequestBody Map<String, String> body) throws IllegalStateTransitionException {
        String party = body.get("party");
        String location = body.get("location");
        actionManager.implementAction(id, party, location);
    }

    @PostMapping("/actions/{id}/complete")
    public void completeAction(@PathVariable Integer id) throws IllegalStateTransitionException {
        actionManager.completeAction(id);
    }

    @PostMapping("/actions/{id}/suspend")
    public void suspendAction(@PathVariable Integer id, @RequestBody(required = false) Map<String, String> body) throws IllegalStateTransitionException {
        String reason = body != null ? body.get("reason") : null;
        actionManager.suspendAction(id, reason);
    }

    @PostMapping("/actions/{id}/resume")
    public void resumeAction(@PathVariable Integer id) throws IllegalStateTransitionException {
        actionManager.resumeAction(id);
    }

    @PostMapping("/actions/{id}/abandon")
    public void abandonAction(@PathVariable Integer id) throws IllegalStateTransitionException {
        actionManager.abandonAction(id);
    }

    @PostMapping("/actions/{id}/allocations")
    public void attachAllocation(@PathVariable Integer id, @RequestBody Map<String, Object> allocation) {
        actionManager.attachResourceAllocation(id, allocation);
    }

    @GetMapping("/actions/{id}/allocations")
    public List<ResourceAllocation> getAllocations(@PathVariable Integer id) {
        return actionManager.getResourceAllocations(id);
    }




    @GetMapping("/accounts/{id}/entries")
    public List<Entry> getEntries(@PathVariable Integer id) {
        return accountManager.getEntriesForAccount(id);
    }

    @GetMapping("/accounts")
    public List<Account> getAccounts() {
        return accountManager.getAllAccounts();
    }



    @GetMapping("/audit-log")
    public List<AuditLogEntry> getAuditLog() {
        return auditLogManager.getAuditLog();
    }
}
