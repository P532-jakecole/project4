package com.project3.Strategy;

import com.project3.DataTypes.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component("WEIGHTED")
public class WeightedScoringStrategy implements DiagnosisStrategy{
    @Override
    public List<Observation> evaluate(AssociativeFunction rule, List<Observation> patientObservations) {
        List<ArgumentWeight> arguments = rule.getArgumentConcepts();
        List<Observation> observations = new ArrayList<>();
        Double threshold = rule.getThreshold();
        Double total = 0.0;
        Date current = new Date();

        for(ArgumentWeight argument : arguments){
            String arg = argument.getConcept();
            Double weight = argument.getWeight();

            for(Observation obs : patientObservations){
                if(obs.getStatus() == ObservationStatus.ACTIVE && obs.getSource() == Source.MANUAL){
                    if(obs instanceof CategoryObservation){
                        if(Objects.equals(((CategoryObservation) obs).getPhenomenon().getName(), arg)  && ((CategoryObservation) obs).getPresence() == Presence.PRESENT){
                            total += weight;
                            observations.add(obs);
                            break;
                        }
                    }else if(obs instanceof Measurement){
                        if(Objects.equals(((Measurement) obs).getPhenomenonType().getName(), arg)){
                            total += weight;
                            observations.add(obs);
                            break;
                        }
                    }
                }
            }
        }

        if(total > threshold){
            return observations;
        }else{
            return null;
        }
    }
}
