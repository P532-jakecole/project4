package com.project4;

import com.project4.Resources.Plan;
import com.project4.Resources.ProposedAction;

public interface PlanNodeVisitor {
    void visitComposite(Plan plan);
    void visitLeaf(ProposedAction action);
}
