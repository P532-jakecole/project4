package com.project4.tests;

import com.project4.Managers.ActionManager;
import com.project4.Repositories.ResourceAccess;
import com.project4.State.*;

import com.project4.Resources.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class InProgressStateTest {

    @Mock
    private SuspendedState suspendedState;

    @Mock
    private AbandonedState abandonedState;

    @Mock
    private CompletedState completedState;

    @Mock
    private ActionContext ctx;

    @Mock
    private ResourceAccess resourceAccess;

    @Mock
    private ActionManager actionManager;

    @Mock
    private ProposedAction action;

    @Mock
    private ImplementedAction implemented;

    @InjectMocks
    private InProgressState inProgressState;

    @Test
    void complete_validTransition_updatesImplementedAction_generatesLedgerEntries_andMovesToCompleted() {
        // Arrange
        when(ctx.getAction()).thenReturn(action);
        when(ctx.getResourceAccess()).thenReturn(resourceAccess);
        when(ctx.getActionManager()).thenReturn(actionManager);
        when(action.getId()).thenReturn(1);
        when(resourceAccess.getImplementedByProposed(1)).thenReturn(implemented);

        // Act
        inProgressState.complete(ctx);

        // Assert
        verify(implemented).setActualStart(any());
        verify(resourceAccess).saveImplementedAction(implemented);
        verify(actionManager).generateLedgerEntries(implemented);
        verify(ctx).setState(completedState);
    }

    @Test
    void suspend_validTransition_createsSuspension_andMovesToSuspended() {
        // Arrange
        when(ctx.getAction()).thenReturn(action);
        when(ctx.getResourceAccess()).thenReturn(resourceAccess);

        // Act
        inProgressState.suspend(ctx, "reason");

        // Assert
        verify(resourceAccess).createSuspension(action, "reason");
        verify(ctx).setState(suspendedState);
    }

    @Test
    void abandon_validTransition_movesToAbandoned() {
        // Act
        inProgressState.abandon(ctx);

        // Assert
        verify(ctx).setState(abandonedState);
    }

    @Test
    void implement_invalidTransition_throwsException() {
        // Act and Assert
        assertThrows(IllegalStateTransitionException.class, () ->
                inProgressState.implement(ctx)
        );

        verify(ctx, never()).setState(any());
    }

    @Test
    void resume_invalidTransition_throwsException() {
        // Act and Assert
        assertThrows(IllegalStateTransitionException.class, () ->
                inProgressState.resume(ctx)
        );

        verify(ctx, never()).setState(any());
    }

    @Test
    void name_returnsInProgress() {
        // Act
        String result = inProgressState.name();

        // Assert
        assertEquals("IN_PROGRESS", result);
    }
}
