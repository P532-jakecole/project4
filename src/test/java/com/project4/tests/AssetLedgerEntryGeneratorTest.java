package com.project4.tests;

import com.project4.AssetLedgerEntryGenerator;
import com.project4.Engines.PostingRuleEngine;
import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssetLedgerEntryGeneratorTest {

    @Mock
    private ResourceAccess resourceAccess;

    @Mock
    private PostingRuleEngine postingRuleEngine;

    @InjectMocks
    private AssetLedgerEntryGenerator generator;

    @Test
    void selectAllocations_validAssetSpecific_returnsFilteredList() {

        // Arrange
        ProposedAction proposed = new ProposedAction();
        ImplementedAction impl = new ImplementedAction();
        impl.setProposedAction(proposed);

        ResourceType assetType = new ResourceType();
        assetType.setKind(ResourceKind.ASSET);

        ResourceAllocation valid = new ResourceAllocation();
        valid.setResourceType(assetType);
        valid.setKind(AllocationKind.SPECIFIC);

        ResourceAllocation invalidKind = new ResourceAllocation();
        invalidKind.setResourceType(assetType);
        invalidKind.setKind(AllocationKind.GENERAL);

        ResourceType consumableType = new ResourceType();
        consumableType.setKind(ResourceKind.CONSUMABLE);

        ResourceAllocation invalidType = new ResourceAllocation();
        invalidType.setResourceType(consumableType);
        invalidType.setKind(AllocationKind.SPECIFIC);

        when(resourceAccess.getAllocationsByAction(any()))
                .thenReturn(List.of(valid, invalidKind, invalidType));

        // Act
        List<ResourceAllocation> result = generator.selectAllocations(impl);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(valid));
    }

    @Test
    void validate_nullTime_throwsException() {

        // Arrange
        ResourceAllocation alloc = new ResourceAllocation();

        // Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> generator.validate(List.of(alloc)));
    }

    @Test
    void validate_invalidTimeRange_throwsException() {

        // Arrange
        ResourceAllocation alloc = new ResourceAllocation();
        alloc.setStartTime(new Date(System.currentTimeMillis() + 1000));
        alloc.setEndTime(new Date());

        // Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> generator.validate(List.of(alloc)));
    }

    @Test
    void buildWithdrawal_validDuration_createsNegativeEntry() {

        // Arrange
        Transaction tx = new Transaction();

        ResourceType type = new ResourceType();
        Account pool = new Account();
        type.setPoolAccount(pool);

        ProposedAction action = new ProposedAction();

        ResourceAllocation alloc = new ResourceAllocation();
        alloc.setResourceType(type);
        alloc.setAction(action);

        Date start = new Date();
        Date end = new Date(start.getTime() + 2 * 60 * 60 * 1000); // 2 hours

        alloc.setStartTime(start);
        alloc.setEndTime(end);

        // Act
        Entry entry = generator.buildWithdrawal(tx, alloc);

        // Assert
        assertEquals(tx, entry.getTransaction());
        assertEquals(pool, entry.getAccount());
        assertEquals(-2.0, entry.getAmount(), 0.01);
    }

    @Test
    void buildDeposit_validDuration_createsPositiveEntry() {

        // Arrange
        Transaction tx = new Transaction();

        ResourceType type = new ResourceType();

        Account usage = new Account();
        when(resourceAccess.getUsageAccount(any())).thenReturn(usage);

        ProposedAction action = new ProposedAction();

        ResourceAllocation alloc = new ResourceAllocation();
        alloc.setResourceType(type);
        alloc.setAction(action);

        Date start = new Date();
        Date end = new Date(start.getTime() + 3 * 60 * 60 * 1000); // 3 hours

        alloc.setStartTime(start);
        alloc.setEndTime(end);

        // Act
        Entry entry = generator.buildDeposit(tx, alloc);

        // Assert
        assertEquals(tx, entry.getTransaction());
        assertEquals(usage, entry.getAccount());
        assertEquals(3.0, entry.getAmount(), 0.01);
    }

    @Test
    void generateEntries_validFlow_postsEntriesAndAppliesRules() {

        // Arrange
        ProposedAction proposed = new ProposedAction();
        proposed.setName("Test Action");

        ImplementedAction impl = new ImplementedAction();
        impl.setProposedAction(proposed);

        ResourceType type = new ResourceType();
        type.setKind(ResourceKind.ASSET);

        Account pool = new Account();
        type.setPoolAccount(pool);

        Account usage = new Account();
        when(resourceAccess.getUsageAccount(any())).thenReturn(usage);

        ResourceAllocation alloc = new ResourceAllocation();
        alloc.setResourceType(type);
        alloc.setKind(AllocationKind.SPECIFIC);
        alloc.setAction(proposed);

        Date start = new Date();
        Date end = new Date(start.getTime() + 60 * 60 * 1000);

        alloc.setStartTime(start);
        alloc.setEndTime(end);

        when(resourceAccess.getAllocationsByAction(any()))
                .thenReturn(List.of(alloc));

        // Act
        Transaction tx = generator.generateEntries(impl);

        // Assert
        assertNotNull(tx);
        verify(resourceAccess, atLeastOnce()).saveEntry(any());
        verify(postingRuleEngine, atLeastOnce()).applyRules(any());
    }
}
