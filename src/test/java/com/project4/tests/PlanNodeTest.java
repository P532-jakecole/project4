package com.project4.tests;

import com.project4.Resources.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanNodeTest {

    @Test
    void getStatus_proposedAction_returnsAssignedStatus() {
        // Arrange
        ProposedAction action = new ProposedAction();
        action.setName("Test Action");
        action.setStatus(ActionStatus.IN_PROGRESS);

        // Act
        ActionStatus result = action.getStatus();

        // Assert
        assertEquals(ActionStatus.IN_PROGRESS, result);
    }


    @Test
    void getStatus_allChildrenCompleted_returnsCompleted() {
        // Arrange
        Plan plan = new Plan();

        plan.setChildren(List.of(
                createAction(ActionStatus.COMPLETED),
                createAction(ActionStatus.COMPLETED)
        ));

        // Act
        ActionStatus result = plan.getStatus();

        // Assert
        assertEquals(ActionStatus.COMPLETED, result);
    }

    @Test
    void getStatus_mixedCompletedAndProposed_returnsInProgress() {
        // Arrange
        Plan plan = new Plan();
        plan.setChildren(List.of(
                createAction(ActionStatus.COMPLETED),
                createAction(ActionStatus.PROPOSED)
        ));

        // Act
        ActionStatus result = plan.getStatus();

        // Assert
        assertEquals(ActionStatus.IN_PROGRESS, result);
    }

    @Test
    void getStatus_anyInProgress_returnsInProgress() {
        // Arrange
        Plan plan = new Plan();

        plan.setChildren(List.of(
                createAction(ActionStatus.COMPLETED),
                createAction(ActionStatus.IN_PROGRESS)
        ));

        // Act
        ActionStatus result = plan.getStatus();

        // Assert
        assertEquals(ActionStatus.IN_PROGRESS, result);
    }

    @Test
    void getStatus_anySuspendedAndNoneInProgress_returnsSuspended() {
        // Arrange
        Plan plan = new Plan();

        plan.setChildren(List.of(
                createAction(ActionStatus.PROPOSED),
                createAction(ActionStatus.SUSPENDED)
        ));

        // Act
        ActionStatus result = plan.getStatus();

        // Assert
        assertEquals(ActionStatus.SUSPENDED, result);
    }

    @Test
    void getStatus_allAbandoned_returnsAbandoned() {
        // Arrange
        Plan plan = new Plan();

        plan.setChildren(List.of(
                createAction(ActionStatus.ABANDONED),
                createAction(ActionStatus.ABANDONED)
        ));

        // Act
        ActionStatus result = plan.getStatus();

        // Assert
        assertEquals(ActionStatus.ABANDONED, result);
    }

    @Test
    void getStatus_emptyChildren_returnsCompleted_edgeCase() {
        // Arrange
        Plan plan = new Plan();
        plan.setChildren(List.of());

        // Act
        ActionStatus result = plan.getStatus();

        // Assert
        assertEquals(ActionStatus.COMPLETED, result);
    }

    private ProposedAction createAction(ActionStatus status) {
        ProposedAction action = new ProposedAction();
        action.setStatus(status);
        return action;
    }
}
