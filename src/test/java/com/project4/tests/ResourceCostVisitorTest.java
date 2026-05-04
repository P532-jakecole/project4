package com.project4.tests;

import com.project4.Repositories.ResourceAccess;
import com.project4.Visitor.*;

import com.project4.Resources.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ResourceCostVisitorTest {

    @Mock
    private ResourceAccess resourceAccess;

    @Mock
    private ResourceAllocation alloc1;

    @Mock
    private ResourceAllocation alloc2;

    @Mock
    private ResourceType type1;

    @Mock
    private ResourceType type2;

    private ProposedAction action(Integer id) {
        ProposedAction a = new ProposedAction();
        // no setId(), so mock behavior instead if needed
        return a;
    }

    @Test
    void visitLeaf_noAllocations_totalCostZero() {
        // Arrange
        ResourceCostVisitor visitor = new ResourceCostVisitor(resourceAccess);

        ProposedAction action = mock(ProposedAction.class);
        when(action.getId()).thenReturn(1);

        when(resourceAccess.getResourceAllocations(1)).thenReturn(List.of());

        // Act
        visitor.visitLeaf(action);

        // Assert
        assertEquals(0.0, visitor.getTotalCost());
    }

    @Test
    void visitLeaf_singleAllocation_calculatesCorrectCost() {
        // Arrange
        ResourceCostVisitor visitor = new ResourceCostVisitor(resourceAccess);

        ProposedAction action = mock(ProposedAction.class);
        when(action.getId()).thenReturn(1);

        when(alloc1.getQuantity()).thenReturn(5.0);
        when(alloc1.getResourceType()).thenReturn(type1);
        when(type1.getUnitCost()).thenReturn(10.0);

        when(resourceAccess.getResourceAllocations(1))
                .thenReturn(List.of(alloc1));

        // Act
        visitor.visitLeaf(action);

        // Assert
        assertEquals(50.0, visitor.getTotalCost());
    }

    @Test
    void visitLeaf_multipleAllocations_accumulatesCost() {
        // Arrange
        ResourceCostVisitor visitor = new ResourceCostVisitor(resourceAccess);

        ProposedAction action = mock(ProposedAction.class);
        when(action.getId()).thenReturn(1);

        when(alloc1.getQuantity()).thenReturn(2.0);
        when(alloc1.getResourceType()).thenReturn(type1);
        when(type1.getUnitCost()).thenReturn(10.0); // 20

        when(alloc2.getQuantity()).thenReturn(3.0);
        when(alloc2.getResourceType()).thenReturn(type2);
        when(type2.getUnitCost()).thenReturn(5.0); // 15

        when(resourceAccess.getResourceAllocations(1))
                .thenReturn(List.of(alloc1, alloc2));

        // Act
        visitor.visitLeaf(action);

        // Assert
        assertEquals(35.0, visitor.getTotalCost());
    }

    @Test
    void visitLeaf_multipleCalls_accumulatesAcrossLeaves() {
        // Arrange
        ResourceCostVisitor visitor = new ResourceCostVisitor(resourceAccess);

        ProposedAction action1 = mock(ProposedAction.class);
        ProposedAction action2 = mock(ProposedAction.class);

        when(action1.getId()).thenReturn(1);
        when(action2.getId()).thenReturn(2);

        when(alloc1.getQuantity()).thenReturn(2.0);
        when(alloc1.getResourceType()).thenReturn(type1);
        when(type1.getUnitCost()).thenReturn(10.0); // 20

        when(alloc2.getQuantity()).thenReturn(4.0);
        when(alloc2.getResourceType()).thenReturn(type2);
        when(type2.getUnitCost()).thenReturn(5.0); // 20

        when(resourceAccess.getResourceAllocations(1))
                .thenReturn(List.of(alloc1));
        when(resourceAccess.getResourceAllocations(2))
                .thenReturn(List.of(alloc2));

        // Act
        visitor.visitLeaf(action1);
        visitor.visitLeaf(action2);

        // Assert
        assertEquals(40.0, visitor.getTotalCost());
    }

    @Test
    void visitComposite_doesNotAffectCost() {
        // Arrange
        ResourceCostVisitor visitor = new ResourceCostVisitor(resourceAccess);
        Plan plan = new Plan();

        // Act
        visitor.visitComposite(plan);

        // Assert
        assertEquals(0.0, visitor.getTotalCost());
    }
}
