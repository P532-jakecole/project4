package com.project3.Observer;

import com.project3.Command.RecordObservationCommand;
import com.project3.Command.RejectObservationCommand;
import com.project3.CommandLog;
import com.project3.DataTypes.CategoryObservation;
import com.project3.DataTypes.Measurement;
import com.project3.DataTypes.Observation;
import com.project3.DataTypes.Patient;
import com.project3.Managers.PatientManager;
import com.project3.OrderAccess;
import com.project3.Strategy.DiagnosisEngine;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;

@Component
public class RuleEvaluationListener {

    private final CommandLog commandLog;
    private final DiagnosisEngine diagnosisEngine;
    private final OrderAccess orderAccess;
    private final PatientManager patientManager;

    public RuleEvaluationListener(CommandLog commandLog, DiagnosisEngine diagnosisEngine, OrderAccess orderAccess, PatientManager patientManager) {
        this.commandLog = commandLog;
        this.diagnosisEngine = diagnosisEngine;
        this.orderAccess = orderAccess;
        this.patientManager = patientManager;
    }

    @EventListener
    @Async
    @Transactional
    public void onObservationCreated(RecordObservationCommand observation) {
        Integer observationId = observation.observationId;
        Integer patientId = observation.patientId;

        Date inferenceTime = new Date();

        ArrayList<Object[]> inferences = patientManager.evaluatePatient(patientId, observation.staff);
        for(Object[] inference : inferences) {
            boolean alreadyInferred = commandLog.checkAuditLogEntered(patientId, inference[0].toString());
            if(!alreadyInferred) {
                commandLog.addAuditLogString("Inference: " + inference[0], observationId, patientId, inferenceTime);
            }
        }
    }

    @EventListener
    @Transactional
    @Async
    public void onObservationRejection(RejectObservationCommand rejectCommand) {
        Integer observationId = rejectCommand.observationId;
        Integer patientId = rejectCommand.patientId;

        Date inferenceTime = new Date();

        ArrayList<Object[]> inferences = patientManager.evaluatePatient(patientId, rejectCommand.staff);
        for(Object[] inference : inferences) {
            boolean alreadyInferred = commandLog.checkAuditLogEntered(patientId, inference[0].toString());
            if(!alreadyInferred) {
                commandLog.addAuditLogString("Inference: " + inference[0], observationId, patientId, inferenceTime);
            }
        }
    }
}
