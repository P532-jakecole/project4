package com.project4;

import com.project4.Resources.Plan;
import com.project4.Resources.ProposedAction;

public interface PlanNodeVisitor {
    void visit(Plan plan);
    void visit(ProposedAction action);
}
