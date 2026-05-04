package com.project4.tests;

import com.project4.Resources.*;
import com.project4.Visitor.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RiskScoreVisitorTest {

    private RiskScoreVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new RiskScoreVisitor();
    }

    @Test
    void visitLeaf_suspendedAction_incrementsScore() {
        // Arrange
        ProposedAction action = new ProposedAction();
        action.setStatus(ActionStatus.SUSPENDED);

        // Act
        visitor.visitLeaf(action);

        // Assert
        assertEquals(1, visitor.getScore());
    }

    @Test
    void visitLeaf_abandonedAction_incrementsScore() {
        // Arrange
        ProposedAction action = new ProposedAction();
        action.setStatus(ActionStatus.ABANDONED);

        // Act
        visitor.visitLeaf(action);

        // Assert
        assertEquals(1, visitor.getScore());
    }

    @Test
    void visitLeaf_completedAction_doesNotIncrementScore() {
        // Arrange
        ProposedAction action = new ProposedAction();
        action.setStatus(ActionStatus.COMPLETED);

        // Act
        visitor.visitLeaf(action);

        // Assert
        assertEquals(0, visitor.getScore());
    }

    @Test
    void visitLeaf_proposedAction_doesNotIncrementScore() {
        // Arrange
        ProposedAction action = new ProposedAction();
        action.setStatus(ActionStatus.PROPOSED);

        // Act
        visitor.visitLeaf(action);

        // Assert
        assertEquals(0, visitor.getScore());
    }

    @Test
    void visitLeaf_multipleMixedActions_correctScoreCalculated() {
        // Arrange
        ProposedAction suspended = new ProposedAction();
        suspended.setStatus(ActionStatus.SUSPENDED);

        ProposedAction abandoned = new ProposedAction();
        abandoned.setStatus(ActionStatus.ABANDONED);

        ProposedAction completed = new ProposedAction();
        completed.setStatus(ActionStatus.COMPLETED);

        ProposedAction proposed = new ProposedAction();
        proposed.setStatus(ActionStatus.PROPOSED);

        // Act
        visitor.visitLeaf(suspended);
        visitor.visitLeaf(abandoned);
        visitor.visitLeaf(completed);
        visitor.visitLeaf(proposed);

        // Assert
        assertEquals(2, visitor.getScore());
    }
}