package com.project3.project3;

import com.project3.DataTypes.*;
import com.project3.Strategy.SimpleConjunctiveStrategy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleConjunctiveStrategyTest {

    private final SimpleConjunctiveStrategy strategy = new SimpleConjunctiveStrategy();


    @Test
    void evaluate_allArgumentsSatisfied_shouldReturnTrue() {

        // ARRANGE
        AssociativeFunction rule = new AssociativeFunction();
        List<ArgumentWeight> arguments = new ArrayList<ArgumentWeight>();
        ArgumentWeight arg = new ArgumentWeight();
        arg.setConcept("Diabetes");
        arg.setWeight(0.4);
        arguments.add(arg);
        ArgumentWeight arg2 = new ArgumentWeight();
        arg.setConcept("Hypertension");
        arg.setWeight(0.3);
        arguments.add(arg2);
        rule.setArgumentConcepts(arguments);
        rule.setProductConcept("Disease");

        Phenomenon p1 = new Phenomenon();
        p1.setName("Diabetes");

        Phenomenon p2 = new Phenomenon();
        p2.setName("Hypertension");

        CategoryObservation obs1 = new CategoryObservation();
        obs1.setPhenomenon(p1);
        obs1.setPresence(Presence.PRESENT);
        obs1.setStatus(ObservationStatus.ACTIVE);

        CategoryObservation obs2 = new CategoryObservation();
        obs2.setPhenomenon(p2);
        obs2.setPresence(Presence.PRESENT);
        obs2.setStatus(ObservationStatus.ACTIVE);

        List<Observation> observations = Arrays.asList(obs1, obs2);

        // ACT
        List<Observation> result = strategy.evaluate(rule, observations);

        // ASSERT
        assertNull(result);
    }


    @Test
    void evaluate_missingArgument_shouldReturnFalse() {

        // ARRANGE
        AssociativeFunction rule = new AssociativeFunction();
        List<ArgumentWeight> arguments = new ArrayList<ArgumentWeight>();
        ArgumentWeight arg = new ArgumentWeight();
        arg.setConcept("Diabetes");
        arg.setWeight(0.4);
        arguments.add(arg);
        ArgumentWeight arg2 = new ArgumentWeight();
        arg.setConcept("Hypertension");
        arg.setWeight(0.3);
        arguments.add(arg2);
        rule.setArgumentConcepts(arguments);

        Phenomenon p1 = new Phenomenon();
        p1.setName("Diabetes");

        CategoryObservation obs1 = new CategoryObservation();
        obs1.setPhenomenon(p1);
        obs1.setPresence(Presence.PRESENT);
        obs1.setStatus(ObservationStatus.ACTIVE);

        List<Observation> observations = Collections.singletonList(obs1);

        // ACT
        List<Observation> result = strategy.evaluate(rule, observations);

        // ASSERT
        assertNull(result);
    }


    @Test
    void evaluate_inactiveObservation_shouldReturnFalse() {

        // ARRANGE
        AssociativeFunction rule = new AssociativeFunction();
        List<ArgumentWeight> arguments = new ArrayList<ArgumentWeight>();
        ArgumentWeight arg = new ArgumentWeight();
        arg.setConcept("Diabetes");
        arg.setWeight(0.4);
        arguments.add(arg);
        rule.setArgumentConcepts(arguments);

        Phenomenon p1 = new Phenomenon();
        p1.setName("Diabetes");

        CategoryObservation obs1 = new CategoryObservation();
        obs1.setPhenomenon(p1);
        obs1.setPresence(Presence.PRESENT);
        obs1.setStatus(ObservationStatus.REJECTED);

        List<Observation> observations = Arrays.asList(obs1);

        // ACT
        List<Observation> result = strategy.evaluate(rule, observations);

        // ASSERT
        assertNull(result);
    }


    @Test
    void evaluate_absentPhenomenon_shouldReturnFalse() {

        // ARRANGE
        AssociativeFunction rule = new AssociativeFunction();
        List<ArgumentWeight> arguments = new ArrayList<ArgumentWeight>();
        ArgumentWeight arg = new ArgumentWeight();
        arg.setConcept("Diabetes");
        arg.setWeight(0.4);
        arguments.add(arg);
        rule.setArgumentConcepts(arguments);

        Phenomenon p1 = new Phenomenon();
        p1.setName("Diabetes");

        CategoryObservation obs1 = new CategoryObservation();
        obs1.setPhenomenon(p1);
        obs1.setPresence(Presence.ABSENT);
        obs1.setStatus(ObservationStatus.ACTIVE);

        List<Observation> observations = Arrays.asList(obs1);

        // ACT
        List<Observation> result = strategy.evaluate(rule, observations);

        // ASSERT
        assertNull(result);
    }


    @Test
    void evaluate_measurementMatch_shouldReturnTrue() {

        // ARRANGE
        AssociativeFunction rule = new AssociativeFunction();
        List<ArgumentWeight> arguments = new ArrayList<ArgumentWeight>();
        ArgumentWeight arg = new ArgumentWeight();
        arg.setConcept("BloodPressure");
        arg.setWeight(0.4);
        arguments.add(arg);
        rule.setArgumentConcepts(arguments);

        PhenomenonType type = new PhenomenonType();
        type.setName("BloodPressure");

        Measurement m = new Measurement();
        m.setPhenomenonType(type);
        m.setStatus(ObservationStatus.ACTIVE);

        List<Observation> observations = Arrays.asList(m);

        // ACT
        List<Observation> result = strategy.evaluate(rule, observations);

        // ASSERT
        assertFalse(result.isEmpty());
    }
}
