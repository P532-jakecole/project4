package com.project4.Visitor;

import com.project4.PlanNodeVisitor;
import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.Plan;
import com.project4.Resources.ProposedAction;
import com.project4.Resources.ResourceAllocation;

import java.util.List;

public class ResourceCostVisitor implements PlanNodeVisitor {

    private final ResourceAccess resourceAccess;

    public ResourceCostVisitor(ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
    }

    private double totalCost = 0.0;

    @Override
    public void visitLeaf(ProposedAction leaf) {
        for (ResourceAllocation alloc : resourceAccess.getResourceAllocations(leaf.getId())) {
            double quantity = alloc.getQuantity();
            double unitCost = alloc.getResourceType().getUnitCost();
            totalCost += quantity * unitCost;
        }
    }

    @Override
    public void visitComposite(Plan plan) {}

    public double getTotalCost() {
        return totalCost;
    }
}
