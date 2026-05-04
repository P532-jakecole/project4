package com.project4.tests;

import com.project4.Managers.ActionManager;
import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.*;
import com.project4.State.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReopenedStateTest {

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
    private ProposedAction proposedAction;

    @Mock
    private ImplementedAction implementedAction;

    @InjectMocks
    private ReopenedState state;

    @Test
    void complete_validTransition_generatesEntries_setsStateCompleted() {

        // Arrange
        when(ctx.getAction()).thenReturn(proposedAction);
        when(ctx.getResourceAccess()).thenReturn(resourceAccess);
        when(ctx.getActionManager()).thenReturn(actionManager);
        when(resourceAccess.getImplementedByProposed(any())).thenReturn(implementedAction);

        // Act
        state.complete(ctx);

        // Assert
        verify(implementedAction).setActualStart(any(Date.class));
        verify(ctx).setImplementedAction(implementedAction);
        verify(actionManager).generateLedgerEntries(implementedAction);
        verify(ctx).setState(completedState);
    }

    @Test
    void abandon_validTransition_setsAbandonedState() {

        // Act
        state.abandon(ctx);

        // Assert
        verify(ctx).setState(abandonedState);
    }

    @Test
    void implement_invalidTransition_throwsException() {

        // Act + Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.implement(ctx));
    }

    @Test
    void name_returnsReopened() {

        // Act
        String result = state.name();

        // Assert
        assertEquals("REOPENED", result);
    }
}
