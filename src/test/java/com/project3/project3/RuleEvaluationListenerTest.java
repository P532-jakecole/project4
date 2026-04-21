package com.project3.project3;

import com.project3.Observer.*;

import com.project3.Command.RecordObservationCommand;
import com.project3.Command.RejectObservationCommand;
import com.project3.DataTypes.Observation;
import com.project3.CommandLog;
import com.project3.OrderAccess;
import com.project3.Managers.PatientManager;
import com.project3.Strategy.DiagnosisEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.Mockito.*;

class RuleEvaluationListenerTest {

    private CommandLog commandLog;
    private DiagnosisEngine diagnosisEngine;
    private OrderAccess orderAccess;
    private PatientManager patientManager;

    private RuleEvaluationListener listener;

    @BeforeEach
    void setUp() {
        commandLog = mock(CommandLog.class);
        diagnosisEngine = mock(DiagnosisEngine.class);
        orderAccess = mock(OrderAccess.class);
        patientManager = mock(PatientManager.class);

        listener = new RuleEvaluationListener(
                commandLog,
                diagnosisEngine,
                orderAccess,
                patientManager
        );
    }


    @Test
    void onObservationCreated_shouldGenerateAndLogInferences() {

        // ARRANGE
        RecordObservationCommand command = mock(RecordObservationCommand.class);
        command.observationId = 1;
        command.patientId = 100;
        command.staff = "staff1";

        ArrayList<Observation> mockObservations = new ArrayList<>();
        when(orderAccess.getObservationsByPatient(100)).thenReturn(mockObservations);
        when(commandLog.checkAuditLogEntered(100, "Diabetes Risk")).thenReturn(false);
        when(commandLog.checkAuditLogEntered(100, "Hypertension Risk")).thenReturn(false);

        ArrayList<Object[]> inferences = new ArrayList<>();
        inferences.add(new Object[]{"Diabetes Risk", 0.5});
        inferences.add(new Object[]{"Hypertension Risk", 0.4});

        when(patientManager.evaluatePatient(100, "staff1"))
                .thenReturn(inferences);

        // ACT
        listener.onObservationCreated(command);

        // ASSERT
        verify(commandLog, times(1))
                .addAuditLogString(eq("Inference: Diabetes Risk"), eq(1), eq(100), any(Date.class));

        verify(commandLog, times(1))
                .addAuditLogString(eq("Inference: Hypertension Risk"), eq(1), eq(100), any(Date.class));
    }

    @Test
    void onObservationRejection_shouldGenerateAndLogInferences() {

        // ARRANGE
        RejectObservationCommand command = mock(RejectObservationCommand.class);
        command.observationId = 2;
        command.patientId = 200;
        command.staff = "staff2";

        ArrayList<Object[]> inferences = new ArrayList<>();
        inferences.add(new Object[]{"Rule A Triggered", 0.4});

        when(patientManager.evaluatePatient(200, "staff2"))
                .thenReturn(inferences);

        // ACT
        listener.onObservationRejection(command);

        // ASSERT
        verify(patientManager, times(1))
                .evaluatePatient(200, "staff2");

        verify(commandLog, times(1))
                .addAuditLogString(eq("Inference: Rule A Triggered"), eq(2), eq(200), any(Date.class));
    }


    @Test
    void shouldNotLogAnything_whenNoInferencesReturned() {

        // ARRANGE
        RecordObservationCommand command = mock(RecordObservationCommand.class);
        command.observationId = 3;
        command.patientId = 300;
        command.staff = "staff3";

        when(orderAccess.getObservationsByPatient(300))
                .thenReturn(new ArrayList<>());

        when(patientManager.evaluatePatient(300, "staff3"))
                .thenReturn(new ArrayList<>());

        // ACT
        listener.onObservationCreated(command);

        // ASSERT
        verify(commandLog, never()).addAuditLogString(any(), any(), any(), any());
    }
}
