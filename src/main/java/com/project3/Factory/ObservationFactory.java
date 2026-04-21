package com.project3.Factory;

import com.project3.DataTypes.*;
import com.project3.Decorator.ObservationRequest;
import com.project3.OrderAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
public class ObservationFactory {

    private final OrderAccess orderAccess;

    public ObservationFactory(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
    }

//    public Measurement createMeasurement(Integer patientId, Integer phenomenonTypeId, Double amount, String unit, Integer protocolId, Date applicabilityTime){
//        Patient patient = orderAccess.findPatient(patientId);
//        PhenomenonType type = orderAccess.findPhenomenonType(phenomenonTypeId);
//        Protocol protocol = protocolId != null ? orderAccess.findProtocol(protocolId) : null;
//
//        if(type.getKind() == PhenomenonKind.QUALITATIVE || !type.getAllowedUnits().contains(unit)){
//            System.out.println("Invalid Measurement Creation");
//            return null;
//        }
//
//        Measurement m = new Measurement();
//        m.setPatient(patient);
//        m.setPhenomenonType(type);
//        m.setAmount(amount);
//        m.setUnit(unit);
//        m.setProtocol(protocol);
//
//        Date now = new Date();
//        m.setRecordingTime(now);
//        m.setApplicabilityTime(applicabilityTime != null ? applicabilityTime : now);
//        m.setStatus(ObservationStatus.ACTIVE);
//        return m;
//    }

    public Measurement createMeasurement(ObservationRequest observation) {
        Patient patient = orderAccess.findPatient(observation.getPatientId());
        PhenomenonType type = orderAccess.findPhenomenonType(observation.getPhenomenonTypeId());
        Protocol protocol = observation.getProtocol() != null ? orderAccess.findProtocol(observation.getProtocol()) : null;


        Measurement m = new Measurement();
        m.setPatient(patient);
        m.setPhenomenonType(type);
        m.setAmount(observation.getAmount());
        m.setUnit(observation.getUnit());
        m.setProtocol(protocol);
        m.setFlag(observation.getAnomaly());

        Date now = new Date();
        m.setRecordingTime(observation.getRecordingTimestamp());
        m.setApplicabilityTime(observation.getApplicableTime() != null ? observation.getApplicableTime() : now);
        m.setStatus(ObservationStatus.ACTIVE);
        return m;
    }

//    public CategoryObservation createCategoryObservation(Integer patientId, Integer phenomenonId, Presence presence, Integer protocolId, Date applicabilityTime){
//
//        Patient patient = orderAccess.findPatient(patientId);
//        Phenomenon phenomenon = orderAccess.getPhenomenaById(phenomenonId);
//        Protocol protocol = protocolId != null ? orderAccess.findProtocol(protocolId) : null;
//
//        if(phenomenon.getPhenomenonType().getKind() == PhenomenonKind.QUANTITATIVE || !phenomenon.getPhenomenonType().getPhenomena().contains(phenomenon)){
//            System.out.println("Invalid Category Observation");
//            return null;
//        }
//
//        CategoryObservation observation = new CategoryObservation();
//        observation.setPatient(patient);
//        observation.setPhenomenon(phenomenon);
//        observation.setPresence(presence);
//        observation.setProtocol(protocol);
//
//        Date now = new Date();
//        observation.setRecordingTime(now);
//        observation.setApplicabilityTime(applicabilityTime != null ? applicabilityTime : now);
//        observation.setStatus(ObservationStatus.ACTIVE);
//        return observation;
//    }

    public CategoryObservation createCategoryObservation(ObservationRequest request) {

        Patient patient = orderAccess.findPatient(request.getPatientId());
        Phenomenon phenomenon = orderAccess.getPhenomenaById(request.getPhenomenonId());
        Protocol protocol = request.getProtocol() != null ? orderAccess.findProtocol(request.getProtocol()) : null;

        CategoryObservation observation = new CategoryObservation();
        observation.setPatient(patient);
        observation.setPhenomenon(phenomenon);
        observation.setPresence(request.getPresence());
        observation.setProtocol(protocol);

        Date now = new Date();
        observation.setRecordingTime(request.getRecordingTimestamp());
        observation.setApplicabilityTime(request.getApplicableTime() != null ? request.getApplicableTime() : now);
        observation.setStatus(ObservationStatus.ACTIVE);
        return observation;
    }

}
