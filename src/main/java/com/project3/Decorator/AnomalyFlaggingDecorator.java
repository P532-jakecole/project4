package com.project3.Decorator;

import com.project3.DataTypes.Observation;
import com.project3.DataTypes.PhenomenonType;
import com.project3.OrderAccess;

public class AnomalyFlaggingDecorator implements ObservationProcessor{
    public ObservationProcessor processor;

    public AnomalyFlaggingDecorator(ObservationProcessor processor) {
        this.processor = processor;
    }

    @Override
    public ObservationRequest process(ObservationRequest observation) throws Exception {
        if(observation == null) {
            processor.process(observation);
            return observation;
        }
        if(!observation.getType().equalsIgnoreCase("measurement")){
            processor.process(observation);
            return observation;
        }


        Double amount = observation.getAmount();

        int typeId = observation.getPhenomenonTypeId();
        OrderAccess orderAccess = observation.getOrderAccess();
        PhenomenonType type = orderAccess.findPhenomenonType(typeId);

        Double lower = type.getNormalMin();
        Double upper = type.getNormalMax();

        if(amount < lower || amount > upper){
            observation.setAnomaly("ANOMALY");
        }

        processor.process(observation);
        return observation;
    }
}
