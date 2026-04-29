package com.project4.Repositories;

import com.project4.Resources.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class ResourceAccess {

    @PersistenceContext
    private EntityManager em;

    public void addProtocol(Protocol protocol) {
        em.persist(protocol);
    }

    public Protocol getProtocol(Integer id) {
        return em.find(Protocol.class, id);
    }

    public List<Protocol> getAllProtocols() {
        return em.createQuery("from Protocol", Protocol.class).getResultList();
    }

    public List<Plan> getPlans(){
        return em.createQuery("from Plan", Plan.class).getResultList();
    }

    public void createResourceType(String name, String kind, String unit){
        ResourceType rt = new ResourceType();
        rt.setName(name);
        rt.setUnit(unit);

        Account account = createAccount(name, rt, AccountKind.POOL);

        Account alertAccount = createAccount(name + "_ALERT", rt, AccountKind.ALERT_MEMO);
        account.setAlertMemoAccount(alertAccount);

        rt.setPoolAccount(account);

        PostingRule rule = new PostingRule();
        rule.setTriggerAccount(account);
        rule.setOutputAccount(alertAccount);
        rule.setStrategyType(StrategyType.OVER_CONSUMPTION);
        em.persist(rule);

        ResourceKind rk = null;

        switch(kind.toLowerCase()){
            case "asset":
                rk = ResourceKind.ASSET;
                break;
            case "consumable":
                rk = ResourceKind.CONSUMABLE;
                break;
        }
        rt.setKind(rk);

        em.persist(rt);
    }

    public void addResourceType(ResourceType type) {
        em.persist(type);
    }

    public ResourceType getResourceType(Integer id) {
        return em.find(ResourceType.class, id);
    }

    public List<ResourceType> getAllResourceTypes() {
        return em.createQuery("from ResourceType", ResourceType.class).getResultList();
    }


    public ImplementedAction getImplementedByProposed(Integer id) {
        List<ImplementedAction> results = em.createQuery(
                        "SELECT i FROM ImplementedAction i WHERE i.proposedAction.id = :id",
                        ImplementedAction.class
                )
                .setParameter("id", id)
                .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

    public List<PostingRule> getRulesByTriggerAccount(Integer accountId) {
        return em.createQuery(
                        "SELECT r FROM PostingRule r WHERE r.triggerAccount.id = :id",
                        PostingRule.class
                ).setParameter("id", accountId)
                .getResultList();
    }

    public void addBalance(Account account, Double amount){
        System.out.println("Added Amount: " + amount);
        Double balance = account.getAmount();
        if(balance == null){
            balance = 0.0;
        }
        balance += amount;
        account.setAmount(balance);
        em.persist(account);
    }

    public void subBalance(Account account, Double amount){
        Double balance = account.getAmount();
        balance -= amount;
        account.setAmount(balance);
        em.persist(account);
    }


    public void addPlan(Plan plan) {
        em.persist(plan);
    }

    public Plan getPlan(Integer id) {
        return em.find(Plan.class, id);
    }

    public void savePlan(Plan plan) {
        em.merge(plan);
    }

    public Account createAccount(String name, ResourceType resourceType, AccountKind accountKind){
        Account account = new Account();
        account.setName(name);
        account.setKind(accountKind);
        account.setResourceType(resourceType);
        account.setAmount(100.0);
        em.persist(account);
        return account;
    }



    public void saveProposedAction(ProposedAction action) {
       if (action.getId() == null) {
            em.persist(action);
        } else {
            em.merge(action);
        }
    }

    public ProposedAction getProposedAction(Integer id) {
        return em.find(ProposedAction.class, id);
    }

    public List<ProposedAction> getProposedActionByProtocol(Integer id) {
        return em.createQuery(
                        "SELECT r FROM ProposedAction r WHERE r.protocol.id = :id",
                        ProposedAction.class
                ).setParameter("id", id)
                .getResultList();
    }



    public void saveImplementedAction(ImplementedAction action) {
        if (action.getId() == null) {
            em.persist(action);
        } else {
            em.merge(action);
        }
    }

    public ImplementedAction getImplementedAction(Integer id) {
        return em.find(ImplementedAction.class, id);
    }



    public void addResourceAllocation(ResourceAllocation allocation) {
        em.persist(allocation);
    }

    public List<ResourceAllocation> getAllocationsByAction(Integer actionId) {
        return em.createQuery(
                "select a from ResourceAllocation a where a.action.id = :id",
                ResourceAllocation.class
        ).setParameter("id", actionId).getResultList();
    }

    public List<ProposedAction> getProposedActions(){
        return em.createQuery("from ProposedAction", ProposedAction.class).getResultList();
    }


    public void addAccount(Account account) {
        em.persist(account);
    }

    public Account getAccount(Integer id) {
        return em.find(Account.class, id);
    }

    public List<Account> getAllAccounts() {
        return em.createQuery("from Account", Account.class).getResultList();
    }

    public Account getUsageAccount(ProposedAction action) {

        List<Account> results = em.createQuery(
                        "select a from Account a where a.name = :actionName and a.kind = :kind",
                        Account.class
                )
                .setParameter("actionName", action.getName())
                .setParameter("kind", AccountKind.USAGE)
                .getResultList();

        if (!results.isEmpty()) {
            return results.get(0);
        }

        Account usage = new Account();
        usage.setName("Usage Account For Action " + action.getId());
        usage.setKind(AccountKind.USAGE);

        //usage.setAction(action);

        em.persist(usage);

        return usage;
    }



    public void addTransaction(Transaction tx) {
        em.persist(tx);
    }

    public Transaction getTransaction(Integer id) {
        return em.find(Transaction.class, id);
    }



    public void addEntry(Entry entry) {
        em.persist(entry);
    }

    public List<Entry> getEntriesByAccount(Integer accountId) {
        return em.createQuery(
                "select e from Entry e where e.account.id = :id order by e.bookedAt desc",
                Entry.class
        ).setParameter("id", accountId).getResultList();
    }

    public Double getAccountBalance(Integer accountId) {
        return em.find(Account.class, accountId).getAmount();
    }



    public void addPostingRule(PostingRule rule) {
        em.persist(rule);
    }

    public List<PostingRule> getPostingRules() {
        return em.createQuery("from PostingRule", PostingRule.class).getResultList();
    }



    public void addAuditLog(AuditLogEntry entry) {
        em.persist(entry);
    }

    public List<AuditLogEntry> getAuditLogs() {
        return em.createQuery(
                "from AuditLogEntry order by timestamp desc",
                AuditLogEntry.class
        ).getResultList();
    }

    public void saveTransaction(Transaction tx) {
        em.persist(tx);
    }

    public void saveResourceAllocation(ResourceAllocation allocation) {
        em.persist(allocation);
    }

    public void saveEntry(Entry w) {
        em.persist(w);
    }

    public void createResourceAllocation(ProposedAction action, String kind, Date start, Date end, Double quantity, Integer resourceTypeId, Integer assetId){
        ResourceAllocation allocation = new ResourceAllocation();
        allocation.setAction(action);
        allocation.setResourceType(getResourceType(resourceTypeId));
        allocation.setAssetId(assetId);
        allocation.setStartTime(start);
        allocation.setQuantity(quantity);
        allocation.setEndTime(end);

        AllocationKind ak = null;
        switch (kind.toLowerCase()){
            case "general":
                ak = AllocationKind.GENERAL;
                break;
            case "specific":
                ak = AllocationKind.SPECIFIC;
                break;
        }
        allocation.setKind(ak);

        em.persist(allocation);
    }

    public List<ResourceAllocation> getResourceAllocations(Integer actionId){
        return em.createQuery(
                "select e from ResourceAllocation e where e.action.id = :id",
                ResourceAllocation.class
        ).setParameter("id", actionId).getResultList();
    }

    public void createSuspension(ProposedAction action, String reason){
        Suspension suspension = new Suspension();
        suspension.setReason(reason);
        suspension.setProposedAction(action);

        em.persist(suspension);
    }

    public void saveAuditLogEntry(AuditLogEntry entry){
        em.persist(entry);
    }


//    public Plan loadPlanTree(Integer planId) {
//        Plan plan = getPlan(planId);
//
//        // Force initialization of children (avoid lazy loading issues)
//        plan.getChildren().size();
//
//        return plan;
//    }
}
