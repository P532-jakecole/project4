package com.project3.project3;

import com.project3.Command.*;

import com.project3.DataTypes.Patient;
import com.project3.OrderAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreatePatientCommandTest {

    private OrderAccess orderAccess;

    @BeforeEach
    void setUp() {
        orderAccess = mock(OrderAccess.class);
    }

    @Test
    void execute_shouldCreatePatient_andCallOrderAccess() {

        // ARRANGE
        Object[] inputs = new Object[]{
                "John Doe",
                "2026-04-14",
                "No prior conditions"
        };

        CreatePatientCommand command =
                new CreatePatientCommand(inputs, orderAccess, "staff1");

        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);

        // ACT
        command.execute();

        // ASSERT
        verify(orderAccess, times(1)).addPatient(patientCaptor.capture());

        Patient captured = patientCaptor.getValue();

        assertEquals("John Doe", captured.getFullName());
        assertEquals(java.sql.Date.valueOf(LocalDate.parse("2026-04-14")), captured.getDateOfBirth());
        assertEquals("No prior conditions", captured.getNote());
    }


    @Test
    void execute_shouldBeRepeatable_andCreateNewPatientEachTime() {

        // ARRANGE
        Object[] inputs = new Object[]{
                "Jane Doe",
                "2026-01-01",
                "Asthma"
        };

        CreatePatientCommand command =
                new CreatePatientCommand(inputs, orderAccess, "staff1");

        // ACT
        command.execute();
        command.execute();

        // ASSERT
        verify(orderAccess, times(2)).addPatient(any(Patient.class));
    }


    @Test
    void execute_shouldAllowEmptyNote() {

        // ARRANGE
        Object[] inputs = new Object[]{
                "Alice Smith",
                "2025-12-12",
                ""
        };

        CreatePatientCommand command =
                new CreatePatientCommand(inputs, orderAccess, "staff1");

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);

        // ACT
        command.execute();

        // ASSERT
        verify(orderAccess).addPatient(captor.capture());

        assertEquals("", captor.getValue().getNote());
    }

    @Test
    void fail_patientUndo() {

        // ARRANGE
        Object[] inputs = new Object[]{
                "Alice Smith",
                "2025-12-12",
                ""
        };

        CreatePatientCommand command =
                new CreatePatientCommand(inputs, orderAccess, "staff1");

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);

        // ACT
        try {
            command.undo();

            // ASSERT
            fail();
        }catch(Exception e) {}
    }
}
