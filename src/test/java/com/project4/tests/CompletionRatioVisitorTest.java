package com.project4.tests;

import com.project4.Visitor.*;

import com.project4.Resources.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompletionRatioVisitorTest {

    private ProposedAction action(ActionStatus status) {
        ProposedAction a = new ProposedAction();
        a.setStatus(status);
        return a;
    }

    @Test
    void getRatio_noLeaves_returnsZero() {
        // Arrange
        CompletionRatioVisitor visitor = new CompletionRatioVisitor();

        // Act
        double ratio = visitor.getRatio();

        // Assert
        assertEquals(0.0, ratio);
    }

    @Test
    void visitLeaf_allCompleted_returnsOne() {
        // Arrange
        CompletionRatioVisitor visitor = new CompletionRatioVisitor();

        ProposedAction a1 = action(ActionStatus.COMPLETED);
        ProposedAction a2 = action(ActionStatus.COMPLETED);

        // Act
        visitor.visitLeaf(a1);
        visitor.visitLeaf(a2);

        double ratio = visitor.getRatio();

        // Assert
        assertEquals(1.0, ratio);
    }

    @Test
    void visitLeaf_noneCompleted_returnsZero() {
        // Arrange
        CompletionRatioVisitor visitor = new CompletionRatioVisitor();

        ProposedAction a1 = action(ActionStatus.PROPOSED);
        ProposedAction a2 = action(ActionStatus.IN_PROGRESS);

        // Act
        visitor.visitLeaf(a1);
        visitor.visitLeaf(a2);

        double ratio = visitor.getRatio();

        // Assert
        assertEquals(0.0, ratio);
    }

    @Test
    void visitLeaf_mixedCompletion_returnsCorrectRatio() {
        // Arrange
        CompletionRatioVisitor visitor = new CompletionRatioVisitor();

        ProposedAction a1 = action(ActionStatus.COMPLETED);
        ProposedAction a2 = action(ActionStatus.PROPOSED);
        ProposedAction a3 = action(ActionStatus.COMPLETED);

        // Act
        visitor.visitLeaf(a1);
        visitor.visitLeaf(a2);
        visitor.visitLeaf(a3);

        double ratio = visitor.getRatio();

        // Assert
        assertEquals(2.0 / 3.0, ratio);
    }

    @Test
    void visitComposite_doesNotAffectCounts() {
        // Arrange
        CompletionRatioVisitor visitor = new CompletionRatioVisitor();

        ProposedAction a1 = action(ActionStatus.COMPLETED);
        Plan plan = new Plan();

        // Act
        visitor.visitLeaf(a1);
        visitor.visitComposite(plan);

        double ratio = visitor.getRatio();

        // Assert
        assertEquals(1.0, ratio);
    }
}
