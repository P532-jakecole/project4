package com.project4.Iterator;

import com.project4.Resources.*;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

public class DepthFirstPlanIterator implements Iterator<PlanNode> {

    private final Stack<Iterator<PlanNode>> stack = new Stack<>();
    private PlanNode next;

    public DepthFirstPlanIterator(PlanNode root) {
        this.next = root;

        if (root instanceof Plan plan && plan.getChildren() != null) {
            stack.push(plan.getChildren().iterator());
        }
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public PlanNode next() {
        if (next == null) {
            throw new NoSuchElementException();
        }

        PlanNode current = next;

        while (!stack.isEmpty()) {

            Iterator<PlanNode> iterator = stack.peek();

            if (iterator.hasNext()) {
                PlanNode node = iterator.next();

                if (node instanceof Plan plan && plan.getChildren() != null) {
                    stack.push(plan.getChildren().iterator());
                }

                next = node;
                return current;
            } else {
                stack.pop();
            }
        }

        next = null;

        return current;
    }
}
