package com.project3.Observer;

import com.project3.Command.RecordObservationCommand;
import com.project3.Command.RejectObservationCommand;
import com.project3.CommandLog;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AuditLogListener {

    private final CommandLog commandLog;

    public AuditLogListener(CommandLog commandLog) {
        this.commandLog = commandLog;
    }

    @EventListener
    @Async
    public void onObservationCreated(RecordObservationCommand observation) {
        String event = "Record";
        Integer observationId = observation.observationId;
        Integer patientId = observation.patientId;
        Date time = observation.timestamp;
        commandLog.addAuditLogString(event, observationId, patientId, time);
    }

    @EventListener
    @Async
    public void onObservationRejection(RejectObservationCommand rejectCommand) {
        String event = "Rejected";
        Integer patientId = rejectCommand.patientId;
        Integer observationId = rejectCommand.observationId;
        Date time = rejectCommand.timestamp;
        commandLog.addAuditLogString(event, observationId, patientId, time);
    }
}
