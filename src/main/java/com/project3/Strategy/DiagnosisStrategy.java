package com.project3.Strategy;

import com.project3.DataTypes.AssociativeFunction;
import com.project3.DataTypes.Observation;

import java.util.List;

public interface DiagnosisStrategy {

    boolean evaluate(AssociativeFunction rule,
                     List<Observation> patientObservations);
}
