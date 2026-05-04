package com.project4;

import com.project4.Engines.PostingRuleEngine;
import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Component
public class ReversalLedgerEntryGenerator extends AbstractLedgerEntryGenerator {
    private final ResourceAccess resourceAccess;
    private final PostingRuleEngine postingRuleEngine;

    public ReversalLedgerEntryGenerator(ResourceAccess resourceAccess, PostingRuleEngine postingRuleEngine) {
        super(resourceAccess, postingRuleEngine);
        this.resourceAccess = resourceAccess;
        this.postingRuleEngine = postingRuleEngine;
    }

    @Override
    protected List<ResourceAllocation> selectAllocations(ImplementedAction action) {

        ProposedAction proposed = action.getProposedAction();
        if (proposed == null) {
            throw new IllegalStateException("ImplementedAction must reference a ProposedAction");
        }

        List<ResourceAllocation> allocations =
                resourceAccess.getAllocationsByAction(proposed.getId());

        if (allocations == null || allocations.isEmpty()) {
            return List.of();
        }

        return allocations.stream()
                .filter(a -> a.getResourceType() != null)
                .filter(a -> a.getResourceType().getKind() == ResourceKind.ASSET)
                .filter(a -> a.getKind() == AllocationKind.SPECIFIC)
                .toList();
    }

    @Override
    protected void validate(List<ResourceAllocation> allocs) {

        for (ResourceAllocation a : allocs) {
            if(a.getResourceType().getKind() == ResourceKind.ASSET){
                if (a.getStart() == null || a.getEnd() == null) {
                    throw new IllegalArgumentException("Allocated time period cannot be null");
                }

                if (a.getStart().after(a.getEnd())) {
                    throw new IllegalArgumentException(
                            "Allocated time period must be positive"
                    );
                }
            }else{
                if (a.getQuantity() == null) {
                    throw new IllegalArgumentException("Allocation quantity cannot be null");
                }

                if (a.getQuantity() <= 0) {
                    throw new IllegalArgumentException(
                            "Consumable allocation must have positive quantity: " + a.getQuantity()
                    );
                }
            }


        }
    }

    @Override
    protected Entry buildWithdrawal(
            Transaction tx, ResourceAllocation a) {
        Entry e = new Entry();
        e.setTransaction(tx);

        Account pool = a.getResourceType().getPoolAccount();
        e.setAccount(pool);

        if(a.getResourceType().getKind() == ResourceKind.ASSET){
            Duration duration = Duration.between(a.getStart().toInstant(), a.getEnd().toInstant());
            Double quantity = duration.toMillis() / 3_600_000.0;

            e.setAmount(quantity);
        }else{
            e.setAmount(a.getQuantity());
        }

        Date now = new Date();
        e.setChargedAt(now);
        e.setBookedAt(now);
        e.setProposedAction(a.getAction());

        // Optional linkage
        //e.setAction(a.getAction());

        return e;
    }

    @Override
    protected Entry buildDeposit(
            Transaction tx, ResourceAllocation a) {
        Entry e = new Entry();
        e.setTransaction(tx);

        Account usage = resourceAccess.getUsageAccount(a.getAction());
        e.setAccount(usage);

        if(a.getResourceType().getKind() == ResourceKind.ASSET){
            Duration duration = Duration.between(a.getStart().toInstant(), a.getEnd().toInstant());
            Double quantity = duration.toMillis() / 3_600_000.0;

            e.setAmount(-quantity);
        }else{
            e.setAmount(-a.getQuantity());
        }

        Date now = new Date();
        e.setChargedAt(now);
        e.setBookedAt(now);
        e.setProposedAction(a.getAction());

        // Optional linkage
        //e.setAction(a.getAction());

        return e;
    }


}
