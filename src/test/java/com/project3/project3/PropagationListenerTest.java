package com.project3.project3;

import com.project3.Observer.*;

import com.project3.Command.RecordObservationCommand;
import com.project3.DataTypes.*;
import com.project3.OrderAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PropagationListenerTest {

    private OrderAccess orderAccess;
    private PropagationListener listener;

    @BeforeEach
    void setUp() {
        orderAccess = mock(OrderAccess.class);
        listener = new PropagationListener(orderAccess);
    }

    @Test
    void testPresentObservation_propagatesUpwards() {
        // Arrange
        Patient patient = mock(Patient.class);
        when(patient.getId()).thenReturn(1);

        Phenomenon parent = mock(Phenomenon.class);
        when(parent.getId()).thenReturn(2);

        Phenomenon child = mock(Phenomenon.class);
        when(child.getId()).thenReturn(3);
        when(child.getParentConcept()).thenReturn(parent);

        CategoryObservation obs = new CategoryObservation();
        obs.setPatient(patient);
        obs.setPhenomenon(child);
        obs.setPresence(Presence.PRESENT);
        obs.setSource(Source.MANUAL);
        obs.setApplicabilityTime(new Date());

        RecordObservationCommand cmd = new RecordObservationCommand(
                new Object[]{"1"}, null, orderAccess, "staff", "category"
        );
        cmd.observationId = 99;

        when(orderAccess.getObservation(99)).thenReturn(obs);
        when(orderAccess.existsObservation(1, 2, Presence.PRESENT)).thenReturn(false);

        // Act
        listener.onObservationCreated(cmd);

        // Assert
        ArgumentCaptor<CategoryObservation> captor =
                ArgumentCaptor.forClass(CategoryObservation.class);

        verify(orderAccess, times(1)).addCategoryObservation(captor.capture());

        CategoryObservation inferred = captor.getValue();

        assertEquals(Presence.PRESENT, inferred.getPresence());
        assertEquals(Source.INFERRED, inferred.getSource());
        assertEquals(parent, inferred.getPhenomenon());
        assertEquals(patient, inferred.getPatient());
    }

    @Test
    void testPresentObservation_doesNotDuplicateIfExists() {
        // Arrange
        Patient patient = mock(Patient.class);
        when(patient.getId()).thenReturn(1);

        Phenomenon parent = mock(Phenomenon.class);
        when(parent.getId()).thenReturn(2);

        Phenomenon child = mock(Phenomenon.class);
        when(child.getId()).thenReturn(3);
        when(child.getParentConcept()).thenReturn(parent);

        CategoryObservation obs = new CategoryObservation();
        obs.setPatient(patient);
        obs.setPhenomenon(child);
        obs.setPresence(Presence.PRESENT);
        obs.setSource(Source.MANUAL);

        RecordObservationCommand cmd = new RecordObservationCommand(
                new Object[]{"1"}, null, orderAccess, "staff", "category"
        );
        cmd.observationId = 99;

        when(orderAccess.getObservation(99)).thenReturn(obs);
        when(orderAccess.existsObservation(1, 2, Presence.PRESENT)).thenReturn(true);

        // Act
        listener.onObservationCreated(cmd);

        // Assert
        verify(orderAccess, never()).addCategoryObservation(any());
    }

    @Test
    void testAbsentObservation_propagatesDownwards() {
        // Arrange
        Patient patient = mock(Patient.class);
        when(patient.getId()).thenReturn(1);

        Phenomenon parent = mock(Phenomenon.class);
        when(parent.getId()).thenReturn(10);

        Phenomenon child = mock(Phenomenon.class);
        when(child.getId()).thenReturn(20);
        when(child.getParentConcept()).thenReturn(parent);

        CategoryObservation obs = new CategoryObservation();
        obs.setPatient(patient);
        obs.setPhenomenon(parent);
        obs.setPresence(Presence.ABSENT);
        obs.setSource(Source.MANUAL);
        obs.setApplicabilityTime(new Date());

        RecordObservationCommand cmd = new RecordObservationCommand(
                new Object[]{"1"}, null, orderAccess, "staff", "category"
        );
        cmd.observationId = 99;

        when(orderAccess.getObservation(99)).thenReturn(obs);
        when(orderAccess.findChildren(10)).thenReturn(List.of(child));
        when(orderAccess.existsObservation(1, 20, Presence.ABSENT)).thenReturn(false);
        when(orderAccess.findChildren(20)).thenReturn(Collections.emptyList());

        // Act
        listener.onObservationCreated(cmd);

        // Assert
        verify(orderAccess, times(1)).addCategoryObservation(any());
    }

    @Test
    void testInferredObservation_doesNothing() {
        // Arrange
        CategoryObservation obs = new CategoryObservation();
        obs.setSource(Source.INFERRED);

        RecordObservationCommand cmd = new RecordObservationCommand(
                new Object[]{"1"}, null, orderAccess, "staff", "category"
        );
        cmd.observationId = 99;

        when(orderAccess.getObservation(99)).thenReturn(obs);

        // Act
        listener.onObservationCreated(cmd);

        // Assert
        verify(orderAccess, never()).addCategoryObservation(any());
    }

    @Test
    void testNonCategoryObservation_doesNothing() {
        // Arrange
        Observation obs = mock(Observation.class);
        when(obs.getSource()).thenReturn(Source.MANUAL);

        RecordObservationCommand cmd = new RecordObservationCommand(
                new Object[]{"1"}, null, orderAccess, "staff", "measurement"
        );
        cmd.observationId = 99;

        when(orderAccess.getObservation(99)).thenReturn(obs);

        // Act
        listener.onObservationCreated(cmd);

        // Assert
        verify(orderAccess, never()).addCategoryObservation(any());
    }
}
