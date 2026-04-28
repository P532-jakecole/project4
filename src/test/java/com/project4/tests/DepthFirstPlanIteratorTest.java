package com.project4.tests;

import com.project4.Resources.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.project4.Iterator.DepthFirstPlanIterator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DepthFirstPlanIteratorTest {

    private ProposedAction action(String name) {
        ProposedAction a = new ProposedAction();
        a.setName(name);
        return a;
    }

    private Plan plan(String name, List<PlanNode> children) {
        Plan p = new Plan();
        p.setName(name);
        p.setChildren(children);
        return p;
    }

    @Test
    void next_singleNodeOnly_returnsRootThenEnds() {
        // Arrange
        ProposedAction root = action("A");

        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(root);

        // Act
        List<PlanNode> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        // Assert
        assertEquals(1, result.size());
        assertEquals("A", ((ProposedAction) result.get(0)).getName());
    }

    @Test
    void next_flatPlan_returnsRootThenChildrenInOrder() {
        // Arrange
        ProposedAction a = action("A");
        ProposedAction b = action("B");

        Plan root = plan("Root", List.of(a, b));

        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(root);

        // Act
        List<PlanNode> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        // Assert
        assertEquals(3, result.size());

        assertEquals("Root", ((Plan) result.get(0)).getName());
        assertEquals("A", ((ProposedAction) result.get(1)).getName());
        assertEquals("B", ((ProposedAction) result.get(2)).getName());
    }

    @Test
    void next_nestedPlan_traversesDepthFirstCorrectly() {
        // Arrange
        ProposedAction a = action("A");
        ProposedAction b = action("B");
        ProposedAction c = action("C");

        Plan subPlan = plan("Sub", List.of(b, c));
        Plan root = plan("Root", List.of(a, subPlan));

        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(root);

        // Act
        List<String> names = new ArrayList<>();
        while (iterator.hasNext()) {
            PlanNode node = iterator.next();

            if (node instanceof Plan p) {
                names.add(p.getName());
            } else if (node instanceof ProposedAction pa) {
                names.add(pa.getName());
            }
        }

        // Assert (DFS order)
        assertEquals(List.of("Root", "A", "Sub", "B", "C"), names);
    }

    @Test
    void next_emptyChildren_handlesGracefully() {
        // Arrange
        Plan root = plan("Root", new ArrayList<>());

        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(root);

        // Act
        List<PlanNode> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        // Assert
        assertEquals(1, result.size());
        assertEquals("Root", ((Plan) result.get(0)).getName());
    }

    @Test
    void next_noMoreElements_throwsException() {
        // Arrange
        ProposedAction root = action("A");
        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(root);

        // Act
        iterator.next();

        // Assert
        assertThrows(java.util.NoSuchElementException.class, iterator::next);
    }
}