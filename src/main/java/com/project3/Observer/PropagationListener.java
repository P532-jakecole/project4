package com.project3.Observer;

import com.project3.Command.RecordObservationCommand;
import com.project3.Command.RejectObservationCommand;
import com.project3.CommandLog;
import com.project3.DataTypes.*;
import com.project3.OrderAccess;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Component
public class PropagationListener {

    private final OrderAccess orderAccess;

    public PropagationListener(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
    }

    @EventListener
    @Transactional
    public void onObservationCreated(RecordObservationCommand command) {

        Observation obs = orderAccess.getObservation(command.observationId);

        if (obs.getSource() == Source.INFERRED) return;

        if (obs instanceof CategoryObservation catObs) {

            System.out.println("In Propagation");

            if (catObs.getPresence() == Presence.PRESENT) {
                Phenomenon current = ((CategoryObservation) obs).getPhenomenon().getParentConcept();

                while (current != null) {

                    boolean alreadyExists = orderAccess.existsObservation(
                            obs.getPatient().getId(),
                            current.getId(),
                            Presence.PRESENT
                    );

                    System.out.println("Already exists: " + alreadyExists);

                    if (!alreadyExists) {
                        CategoryObservation inferred = new CategoryObservation();
                        inferred.setPatient(obs.getPatient());
                        inferred.setPhenomenon(current);
                        inferred.setPresence(Presence.PRESENT);
                        inferred.setSource(Source.INFERRED);

                        inferred.setRecordingTime(new Date());
                        inferred.setApplicabilityTime(obs.getApplicabilityTime());

                        orderAccess.addCategoryObservation(inferred);
                    }

                    current = current.getParentConcept();
                }
            } else {
                propagateDown(catObs);
            }
        }
    }

    @Transactional
    protected void propagateDown(CategoryObservation obs) {

        List<Phenomenon> children =
                orderAccess.findChildren(obs.getPhenomenon().getId());

        for (Phenomenon child : children) {

            boolean alreadyExists = orderAccess.existsObservation(
                    obs.getPatient().getId(),
                    child.getId(),
                    Presence.ABSENT
            );

            if (!alreadyExists) {
                CategoryObservation inferred = new CategoryObservation();
                inferred.setPatient(obs.getPatient());
                inferred.setPhenomenon(child);
                inferred.setPresence(Presence.ABSENT);
                inferred.setSource(Source.INFERRED);

                inferred.setRecordingTime(new Date());
                inferred.setApplicabilityTime(obs.getApplicabilityTime());

                orderAccess.addCategoryObservation(inferred);

                propagateDown(inferred);
            }
        }
    }
}
