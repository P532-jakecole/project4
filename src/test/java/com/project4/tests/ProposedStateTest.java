package com.project4.tests;

import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.*;
import com.project4.State.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ProposedStateTest {

    @Mock
    private InProgressState inProgressState;

    @Mock
    private AbandonedState abandonedState;

    @Mock
    private SuspendedState suspendedState;

    @Mock
    private ActionContext ctx;

    @Mock
    private ResourceAccess resourceAccess;

    @Mock
    private ProposedAction action;

    @InjectMocks
    private ProposedState proposedState;

    @Test
    void implement_validTransition_createsImplementedActionAndMovesToInProgress() {
        // Arrange
        when(ctx.getAction()).thenReturn(action);
        when(ctx.getResourceAccess()).thenReturn(resourceAccess);

        ArgumentCaptor<ImplementedAction> captor =
                ArgumentCaptor.forClass(ImplementedAction.class);

        // Act
        proposedState.implement(ctx);

        // Assert
        verify(resourceAccess).saveImplementedAction(captor.capture());

        ImplementedAction saved = captor.getValue();

        assertNotNull(saved);
        assertEquals(action, saved.getProposedAction());
        assertNotNull(saved.getActualStart());

        verify(ctx).setState(inProgressState);
    }

    @Test
    void suspend_validTransition_movesToSuspended() {
        // Act
        proposedState.suspend(ctx, "reason");

        // Assert
        verify(ctx).setState(suspendedState);
    }

    @Test
    void abandon_validTransition_movesToAbandoned() {
        // Act
        proposedState.abandon(ctx);

        // Assert
        verify(ctx).setState(abandonedState);
    }

    @Test
    void resume_invalidTransition_throwsException() {
        // Act and Assert
        assertThrows(IllegalStateTransitionException.class, () ->
                proposedState.resume(ctx)
        );

        verify(ctx, never()).setState(any());
    }

    @Test
    void complete_invalidTransition_throwsException() {
        // Act and Assert
        assertThrows(IllegalStateTransitionException.class, () ->
                proposedState.complete(ctx)
        );

        verify(ctx, never()).setState(any());
    }

    @Test
    void name_returnsProposed() {
        // Act
        String result = proposedState.name();

        // Assert
        assertEquals("PROPOSED", result);
    }
}