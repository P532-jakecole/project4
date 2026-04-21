package com.project3.project3;

import com.project3.Command.*;

import com.project3.DataTypes.Observation;
import com.project3.DataTypes.Patient;
import com.project3.OrderAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RejectObservationCommandTest {

    private OrderAccess orderAccess;

    @BeforeEach
    void setUp() {
        orderAccess = mock(OrderAccess.class);
    }


    @Test
    void execute_shouldRejectObservation_andSetPatientId() {

        // ARRANGE
        Integer observationId = 10;

        Patient patient = new Patient();
        patient.setFullName("John Doe");

        Observation observation = mock(Observation.class);
        when(observation.getPatient()).thenReturn(patient);

        when(orderAccess.rejectObservation(observationId))
                .thenReturn(observation);

        RejectObservationCommand command =
                new RejectObservationCommand(observationId, "Invalid reading", orderAccess, "staff1");

        // ACT
        command.execute();

        // ASSERT
        verify(orderAccess, times(1)).rejectObservation(observationId);
        assertEquals(patient, observation.getPatient());
    }

    @Test
    void execute_shouldKeepPatientIdNull_whenObservationNotFound() {

        // ARRANGE
        Integer observationId = 20;

        when(orderAccess.rejectObservation(observationId))
                .thenReturn(null);

        RejectObservationCommand command =
                new RejectObservationCommand(observationId, "Not valid", orderAccess, "staff1");

        // ACT
        command.execute();

        // ASSERT
        verify(orderAccess, times(1)).rejectObservation(observationId);
        assertNull(command.patientId);
    }


    @Test
    void constructor_shouldStoreFieldsCorrectly() {

        // ARRANGE
        Integer observationId = 5;
        String reason = "Bad data";

        // ACT
        RejectObservationCommand command =
                new RejectObservationCommand(observationId, reason, orderAccess, "staff1");

        // ASSERT
        assertEquals(observationId, command.observationId);
        assertEquals(reason, command.reason);
        assertNotNull(command.timestamp);
    }

    @Test
    void undo_rejectCommand(){
        // ARRANGE
        Integer observationId = 10;


        Patient patient = new Patient();
        patient.setFullName("John Doe");

        Observation observation = mock(Observation.class);
        when(observation.getPatient()).thenReturn(patient);

        RejectObservationCommand command =
                new RejectObservationCommand(observationId, "Invalid reading", orderAccess, "staff1");

        // ACT
        command.undo();

        // ASSERT
        verify(orderAccess, times(1)).setActiveObservationStatus(observationId);
    }
}
