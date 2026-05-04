package com.project4.tests;

import com.project4.Resources.*;
import com.project4.State.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PendingApprovalStateTest {

    @Mock
    private ProposedState proposedState;

    @Mock
    private InProgressState inProgressState;

    @Mock
    private ActionContext ctx;

    @InjectMocks
    private PendingApprovalState state;

    @Test
    void approve_validTransition_setsInProgressState() {

        // Arrange

        // Act
        state.approve(ctx);

        // Assert
        verify(ctx).setState(inProgressState);
        verify(ctx).setImplementedAction(any(ImplementedAction.class));
    }

    @Test
    void reject_validTransition_setsProposedState() {

        // Arrange

        // Act
        state.reject(ctx);

        // Assert
        verify(ctx).setState(proposedState);
    }

    @Test
    void implement_invalidTransition_throwsException() {

        // Arrange

        // Act + Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.implement(ctx));
    }

    @Test
    void suspend_invalidTransition_throwsException() {

        // Arrange

        // Act + Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.suspend(ctx, "reason"));
    }

    @Test
    void resume_invalidTransition_throwsException() {

        // Arrange

        // Act + Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.resume(ctx));
    }

    @Test
    void complete_invalidTransition_throwsException() {

        // Arrange

        // Act + Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.complete(ctx));
    }

    @Test
    void abandon_invalidTransition_throwsException() {

        // Arrange

        // Act + Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.abandon(ctx));
    }

    @Test
    void submitForApproval_invalidTransition_throwsException() {

        // Arrange

        // Act + Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.submitForApproval(ctx));
    }

    @Test
    void reopen_invalidTransition_throwsException() {

        // Arrange

        // Act + Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.reopen(ctx));
    }

    @Test
    void name_returnsPendingApproval() {

        // Arrange

        // Act
        String result = state.name();

        // Assert
        assertEquals("PENDING_APPROVAL", result);
    }
}
