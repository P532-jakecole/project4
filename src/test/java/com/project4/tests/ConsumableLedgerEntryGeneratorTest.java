package com.project4.tests;

import com.project4.Engines.PostingRuleEngine;
import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.*;
import com.project4.ConsumableLedgerEntryGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ConsumableLedgerEntryGeneratorTest {

    @Mock
    private ResourceAccess resourceAccess;

    @Mock
    private PostingRuleEngine postingRuleEngine;

    @InjectMocks
    private ConsumableLedgerEntryGenerator generator;

    @Test
    void generateEntries_validConsumableAllocation_createsBalancedEntries() {
        // Arrange
        ProposedAction proposedAction = mock(ProposedAction.class);
        when(proposedAction.getId()).thenReturn(1);
        when(proposedAction.getName()).thenReturn("Test Action");

        ImplementedAction implementedAction = new ImplementedAction();
        implementedAction.setProposedAction(proposedAction);

        Account poolAccount = new Account();
        poolAccount.setName("Pool");

        Account usageAccount = new Account();
        usageAccount.setName("Usage");

        ResourceType resourceType = new ResourceType();
        resourceType.setKind(ResourceKind.CONSUMABLE);
        resourceType.setPoolAccount(poolAccount);

        ResourceAllocation allocation = new ResourceAllocation();
        allocation.setAction(proposedAction);
        allocation.setQuantity(5.0);
        allocation.setResourceType(resourceType);

        when(resourceAccess.getAllocationsByAction(1))
                .thenReturn(List.of(allocation));

        when(resourceAccess.getUsageAccount(proposedAction))
                .thenReturn(usageAccount);

        // Act
        Transaction tx = generator.generateEntries(implementedAction);

        // Assert
        assertNotNull(tx);
        assertEquals("Action completion: Test Action", tx.getDescription());

        verify(resourceAccess, times(2)).saveEntry(any(Entry.class));
        verify(postingRuleEngine, times(2)).applyRules(any(Entry.class));
        verify(resourceAccess, times(2)).saveAuditLogEntry(any(AuditLogEntry.class));
    }

    @Test
    void generateEntries_negativeQuantity_throwsException() {
        // Arrange
        ProposedAction proposedAction = mock(ProposedAction.class);
        when(proposedAction.getId()).thenReturn(1);

        ImplementedAction implementedAction = new ImplementedAction();
        implementedAction.setProposedAction(proposedAction);

        ResourceType resourceType = new ResourceType();
        resourceType.setKind(ResourceKind.CONSUMABLE);

        ResourceAllocation allocation = new ResourceAllocation();
        allocation.setAction(proposedAction);
        allocation.setQuantity(-5.0);
        allocation.setResourceType(resourceType);

        when(resourceAccess.getAllocationsByAction(1))
                .thenReturn(List.of(allocation));

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () ->
                generator.generateEntries(implementedAction)
        );

        verify(resourceAccess, never()).saveEntry(any());
    }

    @Test
    void generateEntries_noAllocations_returnsTransactionOnly() {
        // Arrange
        ProposedAction proposedAction = mock(ProposedAction.class);
        when(proposedAction.getId()).thenReturn(1);
        when(proposedAction.getName()).thenReturn("Empty Action");

        ImplementedAction implementedAction = new ImplementedAction();
        implementedAction.setProposedAction(proposedAction);

        when(resourceAccess.getAllocationsByAction(1))
                .thenReturn(List.of());

        // Act
        Transaction tx = generator.generateEntries(implementedAction);

        // Assert
        assertNotNull(tx);

        verify(resourceAccess, never()).saveEntry(any());
        verify(postingRuleEngine, never()).applyRules(any());
    }

    @Test
    void generateEntries_nonConsumableFilteredOut_noEntriesCreated() {
        // Arrange
        ProposedAction proposedAction = mock(ProposedAction.class);
        when(proposedAction.getId()).thenReturn(1);
        when(proposedAction.getName()).thenReturn("Filtered Action");

        ImplementedAction implementedAction = new ImplementedAction();
        implementedAction.setProposedAction(proposedAction);

        ResourceType nonConsumable = new ResourceType();
        nonConsumable.setKind(ResourceKind.ASSET);

        ResourceAllocation allocation = new ResourceAllocation();
        allocation.setAction(proposedAction);
        allocation.setQuantity(5.0);
        allocation.setResourceType(nonConsumable);

        when(resourceAccess.getAllocationsByAction(1))
                .thenReturn(List.of(allocation));

        // Act
        Transaction tx = generator.generateEntries(implementedAction);

        // Assert
        assertNotNull(tx);

        verify(resourceAccess, never()).saveEntry(any());
        verify(postingRuleEngine, never()).applyRules(any());
    }

    @Test
    void generateEntries_missingProposedAction_throwsException() {
        // Arrange
        ImplementedAction implementedAction = new ImplementedAction();
        implementedAction.setProposedAction(null);

        // Act and Assert
        assertThrows(IllegalStateException.class, () ->
                generator.generateEntries(implementedAction)
        );
    }
}
