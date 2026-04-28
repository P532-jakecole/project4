package com.project4;

import com.project4.Engines.PostingRuleEngine;
import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.*;

import java.util.Date;
import java.util.List;

public abstract class AbstractLedgerEntryGenerator {

    private final ResourceAccess resourceAccess;
    private final PostingRuleEngine postingRuleEngine;

    public AbstractLedgerEntryGenerator(ResourceAccess resourceAccess, PostingRuleEngine postingRuleEngine) {
        this.resourceAccess = resourceAccess;
        this.postingRuleEngine = postingRuleEngine;
    }

    public final Transaction generateEntries(
            ImplementedAction action) {
        List<ResourceAllocation> allocs = selectAllocations(action);
        validate(allocs);
        Transaction tx = createTransaction(action);
        for (ResourceAllocation a : allocs) {
            System.out.println("ResourceAllocation: " + a.getResourceType());
            Entry withdrawal = buildWithdrawal(tx, a);
            Entry deposit = buildDeposit(tx, a);
            postEntries(tx, withdrawal, deposit);
        }
        afterPost(tx);
        return tx;
    }
    protected abstract List<ResourceAllocation>
    selectAllocations(ImplementedAction action);
    protected abstract void validate(
            List<ResourceAllocation> allocs);
    protected Entry buildWithdrawal(
            Transaction tx, ResourceAllocation a) {
        Entry e = new Entry();
        e.setTransaction(tx);

        Account pool = a.getResourceType().getPoolAccount();
        e.setAccount(pool);

        e.setAmount(-Math.abs(a.getQuantity()));

        Date now = new Date();
        e.setChargedAt(now);
        e.setBookedAt(now);
        e.setProposedAction(a.getAction());

        // Optional linkage
        //e.setAction(a.getAction());

        return e;
    }
    protected Entry buildDeposit(
            Transaction tx, ResourceAllocation a) {
        Entry e = new Entry();
        e.setTransaction(tx);

        Account usage = resourceAccess.getUsageAccount(a.getAction());
        e.setAccount(usage);

        e.setAmount(Math.abs(a.getQuantity()));

        Date now = new Date();
        e.setChargedAt(now);
        e.setBookedAt(now);
        e.setProposedAction(a.getAction());

        // Optional linkage
        //e.setAction(a.getAction());

        return e;
    }
    protected void afterPost(Transaction tx) {}
    private Transaction createTransaction(ImplementedAction a)
    {
        Transaction tx = new Transaction();
        tx.setDescription("Action completion: " + a.getProposedAction().getName());
        tx.setCreatedAt(new Date());

        resourceAccess.saveTransaction(tx);

        return tx;
    }
    private void postEntries(Transaction tx,
                             Entry w, Entry d) {
        System.out.println("Entry w: " + w.getAccount().getName());
        System.out.println("Entry d: " + d.getAccount().getName());
        if (w == null || d == null) {
            throw new IllegalStateException("An Entry cannot be null");
        }

        double total = w.getAmount() + d.getAmount();
        if (Math.abs(total) > 0.001) {
            throw new IllegalStateException("Ledger entries are not balanced");
        }

        resourceAccess.saveEntry(w);
        resourceAccess.saveEntry(d);



//        tx.getEntries().add(w);
//        tx.getEntries().add(d);

        postingRuleEngine.applyRules(w);
        postingRuleEngine.applyRules(d);
        createAuditEntry("Withdraw", w.getAccount().getId(), w.getId());
        createAuditEntry("Deposit", d.getAccount().getId(), d.getId());

    }

    private void createAuditEntry(String event, Integer accountId, Integer entryId) {
        AuditLogEntry auditLogEntry = new AuditLogEntry();
        auditLogEntry.setAccountId(accountId);
        auditLogEntry.setEntryId(entryId);
        auditLogEntry.setEvent(event);
        auditLogEntry.setTimestamp(new Date());

        System.out.println("Created AuditEntry for: " + event);

        resourceAccess.saveAuditLogEntry(auditLogEntry);
    }

}
