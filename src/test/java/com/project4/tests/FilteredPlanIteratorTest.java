package com.project4.tests;

import com.project4.Iterator.DepthFirstPlanIterator;
import com.project4.Iterator.FilteredPlanIterator;
import com.project4.Resources.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FilteredPlanIteratorTest {

    @Mock
    private DepthFirstPlanIterator inner;

    @Mock
    private PlanNode node1;

    @Mock
    private PlanNode node2;

    @Mock
    private PlanNode node3;

    @Test
    void hasNext_withMatchingElements_returnsTrue() {
        // Arrange
        when(inner.hasNext()).thenReturn(true, true, false);
        when(inner.next()).thenReturn(node1, node2);

        Predicate<PlanNode> predicate = n -> n == node2;

        FilteredPlanIterator iterator = new FilteredPlanIterator(inner, predicate);

        // Act
        boolean result = iterator.hasNext();

        // Assert
        assertTrue(result);
    }

    @Test
    void next_returnsOnlyMatchingElements() {
        // Arrange
        when(inner.hasNext()).thenReturn(true, true, true, false);
        when(inner.next()).thenReturn(node1, node2, node3);

        Predicate<PlanNode> predicate = n -> n == node2;

        FilteredPlanIterator iterator = new FilteredPlanIterator(inner, predicate);

        // Act
        PlanNode result = iterator.next();

        // Assert
        assertEquals(node2, result);
        assertFalse(iterator.hasNext());
    }

    @Test
    void next_multipleMatches_iteratesCorrectly() {
        // Arrange
        when(inner.hasNext()).thenReturn(true, true, true, true, false);
        when(inner.next()).thenReturn(node1, node2, node3, node2);

        Predicate<PlanNode> predicate = n -> n == node2;

        FilteredPlanIterator iterator = new FilteredPlanIterator(inner, predicate);

        // Act
        PlanNode first = iterator.next();
        PlanNode second = iterator.next();

        // Assert
        assertEquals(node2, first);
        assertEquals(node2, second);
        assertFalse(iterator.hasNext());
    }

    @Test
    void hasNext_noMatchingElements_returnsFalse() {
        // Arrange
        when(inner.hasNext()).thenReturn(true, true, false);
        when(inner.next()).thenReturn(node1, node3);

        Predicate<PlanNode> predicate = n -> false;

        FilteredPlanIterator iterator = new FilteredPlanIterator(inner, predicate);

        // Act
        boolean result = iterator.hasNext();

        // Assert
        assertFalse(result);
    }

    @Test
    void next_noMatchingElements_throwsException() {
        // Arrange
        when(inner.hasNext()).thenReturn(true, false);
        when(inner.next()).thenReturn(node1);

        Predicate<PlanNode> predicate = n -> false;

        FilteredPlanIterator iterator = new FilteredPlanIterator(inner, predicate);

        // Act & Assert
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void next_calledUntilExhausted_thenThrowsException() {
        // Arrange
        when(inner.hasNext()).thenReturn(true, false);
        when(inner.next()).thenReturn(node2);

        Predicate<PlanNode> predicate = n -> true;

        FilteredPlanIterator iterator = new FilteredPlanIterator(inner, predicate);

        // Act
        PlanNode first = iterator.next();

        // Assert
        assertEquals(node2, first);
        assertFalse(iterator.hasNext());

        assertThrows(NoSuchElementException.class, iterator::next);
    }
}
