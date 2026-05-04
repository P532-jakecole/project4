package com.project4.Visitor;

import com.project4.PlanNodeVisitor;
import com.project4.Resources.ActionStatus;
import com.project4.Resources.Plan;
import com.project4.Resources.ProposedAction;

public class CompletionRatioVisitor implements PlanNodeVisitor {

    private int totalLeaves = 0;
    private int completedLeaves = 0;

    @Override
    public void visitLeaf(ProposedAction leaf) {
        totalLeaves++;
        if (leaf.getStatus() == ActionStatus.COMPLETED) {
            completedLeaves++;
        }
    }

    @Override
    public void visitComposite(Plan plan) {}

    public double getRatio() {
        if (totalLeaves == 0) return 0.0;
        return (double) completedLeaves / totalLeaves;
    }
}
