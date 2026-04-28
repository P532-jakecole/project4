package com.project4.tests;

import com.project4.State.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SuspendedStateTest {

    @Mock
    private AbandonedState abandonedState;

    @Mock
    private ProposedState proposedState;

    @Mock
    private ActionContext ctx;

    @InjectMocks
    private SuspendedState suspendedState;

    @Test
    void resume_validTransition_movesToProposed() {
        // Act
        suspendedState.resume(ctx);

        // Assert
        verify(ctx).setState(proposedState);
    }

    @Test
    void abandon_validTransition_movesToAbandoned() {
        // Act
        suspendedState.abandon(ctx);

        // Assert
        verify(ctx).setState(abandonedState);
    }

    @Test
    void implement_invalidTransition_throwsException() {
        // Act and Assert
        assertThrows(IllegalStateTransitionException.class, () ->
                suspendedState.implement(ctx)
        );

        verify(ctx, never()).setState(any());
    }

    @Test
    void suspend_invalidTransition_throwsException() {
        // Act and Assert
        assertThrows(IllegalStateTransitionException.class, () ->
                suspendedState.suspend(ctx, "reason")
        );

        verify(ctx, never()).setState(any());
    }

    @Test
    void complete_invalidTransition_throwsException() {
        // Act and Assert
        assertThrows(IllegalStateTransitionException.class, () ->
                suspendedState.complete(ctx)
        );

        verify(ctx, never()).setState(any());
    }

    @Test
    void name_returnsSuspended() {
        // Act
        String result = suspendedState.name();

        // Assert
        assertEquals("SUSPENDED", result);
    }
}
