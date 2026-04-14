package com.project3.Strategy;

import com.project3.DataTypes.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SimpleConjunctiveStrategy implements DiagnosisStrategy{
    @Override
    public boolean evaluate(AssociativeFunction rule, List<Observation> patientObservations) {
        String[] argument = rule.getArgumentConcepts();
        Date current = new Date();
        boolean currentFound;
        for(String arg : argument){
            currentFound = false;
            for(Observation obs : patientObservations){
                if(obs.getApplicabilityTime().after(current)){
                    if(obs instanceof CategoryObservation){
                        if(Objects.equals(((CategoryObservation) obs).getPhenomenon().getName(), arg)  && ((CategoryObservation) obs).getPresence() == Presence.PRESENT){
                            currentFound = true;
                            break;
                        }
                    }else if(obs instanceof Measurement){
                        if(Objects.equals(((Measurement) obs).getPhenomenonType().getName(), arg)){
                            currentFound = true;
                            break;
                        }
                    }
                }
            }
            if(!currentFound){
                return false;
            }
        }


        return true;
    }
}
