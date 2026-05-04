package com.project4.tests;

import com.project4.Iterator.LazySubtreeIterator;
import com.project4.Resources.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LazySubtreeIteratorTest {

    private ProposedAction action(String name, ActionStatus status) {
        ProposedAction a = new ProposedAction();
        a.setName(name);
        a.setStatus(status);
        return a;
    }

    private Plan plan(String name, PlanNode... children) {
        Plan p = new Plan();
        p.setName(name);
        p.setChildren(List.of(children));
        return p;
    }

    @Test
    void next_depthLimitZero_returnsOnlyRoot() {
        // Arrange
        ProposedAction child = action("child", ActionStatus.PROPOSED);
        Plan root = plan("root", child);

        LazySubtreeIterator iterator = new LazySubtreeIterator(root, 0);

        // Act
        PlanNode first = iterator.next();

        // Assert
        assertEquals(root, first);
        assertFalse(iterator.hasNext());
    }

    @Test
    void next_depthLimitOne_traversesOnlyImmediateChildren() {
        // Arrange
        ProposedAction child1 = action("child1", ActionStatus.PROPOSED);
        ProposedAction child2 = action("child2", ActionStatus.PROPOSED);
        Plan root = plan("root", child1, child2);

        LazySubtreeIterator iterator = new LazySubtreeIterator(root, 1);

        // Act
        PlanNode first = iterator.next();
        PlanNode second = iterator.next();
        PlanNode third = iterator.next();

        // Assert
        assertEquals(root, first);
        assertEquals(child1, second);
        assertEquals(child2, third);
        assertFalse(iterator.hasNext());
    }

    @Test
    void next_depthLimitRestrictsTraversal_stopsAtLimit() {
        // Arrange
        ProposedAction leaf = action("leaf", ActionStatus.PROPOSED);
        Plan subPlan = plan("subPlan", leaf);
        Plan root = plan("root", subPlan);

        LazySubtreeIterator iterator = new LazySubtreeIterator(root, 1);

        // Act
        PlanNode first = iterator.next();
        PlanNode second = iterator.next();

        // Assert
        assertEquals(root, first);
        assertEquals(subPlan, second);
        assertFalse(iterator.hasNext());
    }

    @Test
    void next_depthLimitAllowsDeeperTraversal_includesNestedChildren() {
        // Arrange
        ProposedAction leaf = action("leaf", ActionStatus.PROPOSED);
        Plan subPlan = plan("subPlan", leaf);
        Plan root = plan("root", subPlan);

        LazySubtreeIterator iterator = new LazySubtreeIterator(root, 2);

        // Act
        PlanNode first = iterator.next();
        PlanNode second = iterator.next();
        PlanNode third = iterator.next();

        // Assert
        assertEquals(root, first);
        assertEquals(subPlan, second);
        assertEquals(leaf, third);
        assertFalse(iterator.hasNext());
    }

    @Test
    void hasNext_emptyAfterTraversal_returnsFalse() {
        // Arrange
        ProposedAction child = action("child", ActionStatus.PROPOSED);
        Plan root = plan("root", child);

        LazySubtreeIterator iterator = new LazySubtreeIterator(root, 1);

        // Act
        iterator.next();
        iterator.next();

        // Assert
        assertFalse(iterator.hasNext());
    }

    @Test
    void next_calledAfterExhaustion_throwsException() {
        // Arrange
        ProposedAction child = action("child", ActionStatus.PROPOSED);
        Plan root = plan("root", child);

        LazySubtreeIterator iterator = new LazySubtreeIterator(root, 1);

        // Act
        iterator.next();
        iterator.next();

        // Assert
        assertThrows(NoSuchElementException.class, iterator::next);
    }
}
