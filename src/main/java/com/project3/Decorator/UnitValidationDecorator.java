package com.project3.Decorator;

import com.project3.DataTypes.Observation;
import com.project3.DataTypes.PhenomenonKind;
import com.project3.DataTypes.PhenomenonType;
import com.project3.DataTypes.Protocol;
import com.project3.OrderAccess;

public class UnitValidationDecorator implements ObservationProcessor{
    public ObservationProcessor processor;

    public UnitValidationDecorator(ObservationProcessor processor) {
        this.processor = processor;
    }


    @Override
    public ObservationRequest process(ObservationRequest observation) throws Exception {
        if(observation == null) {
            processor.process(observation);
            return observation;
        }
        int typeId = observation.getPhenomenonTypeId();
        if(typeId == -1){
            processor.process(observation);
            return observation;
        }
        OrderAccess orderAccess = observation.getOrderAccess();
        PhenomenonType type = orderAccess.findPhenomenonType(typeId);

        if(!type.getAllowedUnits().contains(observation.getUnit())){
            throw new Exception();
        }

        processor.process(observation);
        return observation;
    }
}
