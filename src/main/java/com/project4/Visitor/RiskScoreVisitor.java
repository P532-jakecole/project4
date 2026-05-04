package com.project4.Visitor;

import com.project4.PlanNodeVisitor;
import com.project4.Resources.ActionStatus;
import com.project4.Resources.Plan;
import com.project4.Resources.ProposedAction;

public class RiskScoreVisitor implements PlanNodeVisitor {

    private int score = 0;

    @Override
    public void visitLeaf(ProposedAction leaf) {
        if (leaf.getStatus() == ActionStatus.SUSPENDED ||
                leaf.getStatus() == ActionStatus.ABANDONED) {
            score++;
        }
    }

    @Override
    public void visitComposite(Plan plan) {}

    public int getScore() {
        return score;
    }
}
