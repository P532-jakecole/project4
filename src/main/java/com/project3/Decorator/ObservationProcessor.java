package com.project3.Decorator;

import com.project3.DataTypes.Observation;

public interface ObservationProcessor {
    ObservationRequest process(ObservationRequest observation) throws Exception;
}
