package com.project4;

import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.ImplementedAction;
import com.project4.Resources.ProposedAction;
import com.project4.Resources.ResourceAllocation;
import com.project4.Resources.ResourceKind;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsumableLedgerEntryGenerator extends AbstractLedgerEntryGenerator {
    private final ResourceAccess resourceAccess;

    public ConsumableLedgerEntryGenerator(ResourceAccess resourceAccess) {
        super(resourceAccess);
        this.resourceAccess = resourceAccess;
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
                .filter(a -> a.getResourceType().getKind() == ResourceKind.CONSUMABLE)
                .toList();
    }

    @Override
    protected void validate(List<ResourceAllocation> allocs) {

        for (ResourceAllocation a : allocs) {

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
