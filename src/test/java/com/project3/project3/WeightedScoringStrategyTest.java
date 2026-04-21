package com.project3.project3;

import com.project3.Strategy.*;

import com.project3.DataTypes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeightedScoringStrategyTest {

    private WeightedScoringStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new WeightedScoringStrategy();
    }

    @Test
    void testEvaluate_returnsObservations_whenThresholdExceeded() {
        // Arrange
        AssociativeFunction rule = mock(AssociativeFunction.class);

        ArgumentWeight arg1 = mock(ArgumentWeight.class);
        when(arg1.getConcept()).thenReturn("Fever");
        when(arg1.getWeight()).thenReturn(0.6);

        ArgumentWeight arg2 = mock(ArgumentWeight.class);
        when(arg2.getConcept()).thenReturn("Cough");
        when(arg2.getWeight()).thenReturn(0.5);

        when(rule.getArgumentConcepts()).thenReturn(List.of(arg1, arg2));
        when(rule.getThreshold()).thenReturn(0.9);

        Phenomenon feverPhen = mock(Phenomenon.class);
        when(feverPhen.getName()).thenReturn("Fever");

        CategoryObservation feverObs = mock(CategoryObservation.class);
        when(feverObs.getStatus()).thenReturn(ObservationStatus.ACTIVE);
        when(feverObs.getPhenomenon()).thenReturn(feverPhen);
        when(feverObs.getPresence()).thenReturn(Presence.PRESENT);
        when(feverObs.getSource()).thenReturn(Source.MANUAL);

        Phenomenon coughPhen = mock(Phenomenon.class);
        when(coughPhen.getName()).thenReturn("Cough");

        CategoryObservation coughObs = mock(CategoryObservation.class);
        when(coughObs.getStatus()).thenReturn(ObservationStatus.ACTIVE);
        when(coughObs.getPhenomenon()).thenReturn(coughPhen);
        when(coughObs.getPresence()).thenReturn(Presence.PRESENT);
        when(coughObs.getSource()).thenReturn(Source.MANUAL);

        List<Observation> observations = List.of(feverObs, coughObs);

        // Act
        List<Observation> result = strategy.evaluate(rule, observations);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(feverObs));
        assertTrue(result.contains(coughObs));
    }

    @Test
    void testEvaluate_returnsNull_whenThresholdNotMet() {
        // Arrange
        AssociativeFunction rule = mock(AssociativeFunction.class);

        ArgumentWeight arg = mock(ArgumentWeight.class);
        when(arg.getConcept()).thenReturn("Fever");
        when(arg.getWeight()).thenReturn(0.5);

        when(rule.getArgumentConcepts()).thenReturn(List.of(arg));
        when(rule.getThreshold()).thenReturn(1.0);

        Phenomenon phen = mock(Phenomenon.class);
        when(phen.getName()).thenReturn("Fever");

        CategoryObservation obs = mock(CategoryObservation.class);
        when(obs.getStatus()).thenReturn(ObservationStatus.ACTIVE);
        when(obs.getPhenomenon()).thenReturn(phen);
        when(obs.getPresence()).thenReturn(Presence.PRESENT);

        // Act
        List<Observation> result = strategy.evaluate(rule, List.of(obs));

        // Assert
        assertNull(result);
    }

    @Test
    void testEvaluate_ignoresInactiveObservations() {
        // Arrange
        AssociativeFunction rule = mock(AssociativeFunction.class);

        ArgumentWeight arg = mock(ArgumentWeight.class);
        when(arg.getConcept()).thenReturn("Fever");
        when(arg.getWeight()).thenReturn(1.0);

        when(rule.getArgumentConcepts()).thenReturn(List.of(arg));
        when(rule.getThreshold()).thenReturn(0.5);

        Phenomenon phen = mock(Phenomenon.class);
        when(phen.getName()).thenReturn("Fever");

        CategoryObservation obs = mock(CategoryObservation.class);
        when(obs.getStatus()).thenReturn(ObservationStatus.REJECTED); // inactive
        when(obs.getPhenomenon()).thenReturn(phen);

        // Act
        List<Observation> result = strategy.evaluate(rule, List.of(obs));

        // Assert
        assertNull(result);
    }

    @Test
    void testEvaluate_handlesMeasurementMatch() {
        // Arrange
        AssociativeFunction rule = mock(AssociativeFunction.class);

        ArgumentWeight arg = mock(ArgumentWeight.class);
        when(arg.getConcept()).thenReturn("Weight");
        when(arg.getWeight()).thenReturn(1.0);

        when(rule.getArgumentConcepts()).thenReturn(List.of(arg));
        when(rule.getThreshold()).thenReturn(0.5);

        PhenomenonType type = mock(PhenomenonType.class);
        when(type.getName()).thenReturn("Weight");

        Measurement measurement = mock(Measurement.class);
        when(measurement.getStatus()).thenReturn(ObservationStatus.ACTIVE);
        when(measurement.getPhenomenonType()).thenReturn(type);
        when(measurement.getSource()).thenReturn(Source.MANUAL);

        // Act
        List<Observation> result = strategy.evaluate(rule, List.of(measurement));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(measurement));
    }

    @Test
    void testEvaluate_partialMatch_onlyCountsMatchingArguments() {
        // Arrange
        AssociativeFunction rule = mock(AssociativeFunction.class);

        ArgumentWeight arg1 = mock(ArgumentWeight.class);
        when(arg1.getConcept()).thenReturn("Fever");
        when(arg1.getWeight()).thenReturn(0.6);

        ArgumentWeight arg2 = mock(ArgumentWeight.class);
        when(arg2.getConcept()).thenReturn("Cough");
        when(arg2.getWeight()).thenReturn(0.6);

        when(rule.getArgumentConcepts()).thenReturn(List.of(arg1, arg2));
        when(rule.getThreshold()).thenReturn(0.5);

        Phenomenon phen = mock(Phenomenon.class);
        when(phen.getName()).thenReturn("Fever");

        CategoryObservation obs = mock(CategoryObservation.class);
        when(obs.getStatus()).thenReturn(ObservationStatus.ACTIVE);
        when(obs.getPhenomenon()).thenReturn(phen);
        when(obs.getPresence()).thenReturn(Presence.PRESENT);
        when(obs.getSource()).thenReturn(Source.MANUAL);

        // Act
        List<Observation> result = strategy.evaluate(rule, List.of(obs));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(obs));
    }
}
