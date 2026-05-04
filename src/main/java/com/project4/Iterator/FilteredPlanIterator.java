package com.project4.Iterator;

import com.project4.Resources.PlanNode;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class FilteredPlanIterator implements Iterator<PlanNode> {

    private final DepthFirstPlanIterator inner;
    private final Predicate<PlanNode> predicate;
    private PlanNode next;

    public FilteredPlanIterator(DepthFirstPlanIterator inner, Predicate<PlanNode> predicate) {
        this.inner = inner;
        this.predicate = predicate;
        advance();
    }

    private void advance() {
        next = null;
        while (inner.hasNext()) {
            PlanNode candidate = inner.next();
            if (predicate.test(candidate)) {
                next = candidate;
                break;
            }
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
        advance();
        return current;
    }
}
