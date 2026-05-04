package com.project4.tests;

import com.project4.Managers.ActionManager;
import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.*;
import com.project4.State.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompletedStateTest {

    @Mock
    private ReopenedState reopenedState;

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
    private CompletedState state;

    @Test
    void reopen_validTransition_generatesReverseEntries_andSetsReopenedState() {

        // Arrange
        when(ctx.getAction()).thenReturn(proposedAction);
        when(ctx.getResourceAccess()).thenReturn(resourceAccess);
        when(ctx.getActionManager()).thenReturn(actionManager);
        when(resourceAccess.getImplementedByProposed(any())).thenReturn(implementedAction);

        // Act
        state.reopen(ctx);

        // Assert
        verify(actionManager).generateReverseLedgerEntries(implementedAction);
        verify(ctx).setState(reopenedState);
    }

    @Test
    void implement_invalidTransition_throwsException() {

        // Arrange

        // Act + Assert
        assertThrows(IllegalStateTransitionException.class,
                () -> state.implement(ctx));
    }

    @Test
    void name_returnsCompleted() {

        // Act
        String result = state.name();

        // Assert
        assertEquals("COMPLETED", result);
    }
}