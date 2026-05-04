package com.project4.Iterator;

import com.project4.Resources.Plan;
import com.project4.Resources.PlanNode;
import java.util.NoSuchElementException;

import java.util.Iterator;
import java.util.Stack;

public class LazySubtreeIterator implements Iterator<PlanNode> {

    private final Stack<int[]> depthStack = new Stack<>(); // int[0] = current depth
    private final Stack<Iterator<PlanNode>> iterStack = new Stack<>();
    private final int depthLimit;
    private PlanNode next;

    public LazySubtreeIterator(PlanNode root, int depthLimit) {
        this.depthLimit = depthLimit;
        this.next = root;

        if (root instanceof Plan plan && plan.getChildren() != null && depthLimit > 0) {
            iterStack.push(plan.getChildren().iterator());
            depthStack.push(new int[]{1});
        }
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public PlanNode next() {
        if (next == null) throw new NoSuchElementException();

        PlanNode current = next;

        while (!iterStack.isEmpty()) {
            Iterator<PlanNode> iter = iterStack.peek();
            int currentDepth = depthStack.peek()[0];

            if (iter.hasNext()) {
                PlanNode node = iter.next();

                if (currentDepth < depthLimit
                        && node instanceof Plan plan
                        && plan.getChildren() != null) {
                    iterStack.push(plan.getChildren().iterator());
                    depthStack.push(new int[]{currentDepth + 1});
                }

                next = node;
                return current;
            } else {
                iterStack.pop();
                depthStack.pop();
            }
        }

        next = null;
        return current;
    }
}
