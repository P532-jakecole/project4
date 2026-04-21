package com.project3.Decorator;

import com.project3.DataTypes.Observation;

import java.util.Date;

public class AuditStampingDecorator implements ObservationProcessor{
    public ObservationProcessor processor;
    public String user;

    public AuditStampingDecorator(ObservationProcessor processor, String user) {
        this.processor = processor;
        this.user = user;
    }

    @Override
    public ObservationRequest process(ObservationRequest observation) throws Exception {
        if(observation == null) {
            processor.process(observation);
            return observation;
        }
        Date recordingTimestamp = new Date();
        observation.setRecordingTimestamp(recordingTimestamp);
        observation.setUser(user);

        processor.process(observation);
        return observation;
    }
}
