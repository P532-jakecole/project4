package com.project3.Managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project3.Command.CreatePatientCommand;
import com.project3.Command.RecordObservationCommand;
import com.project3.CommandLog;
import com.project3.DataTypes.*;
import com.project3.OrderAccess;
import com.project3.Strategy.DiagnosisEngine;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientManager {
    private final OrderAccess orderAccess;
    private final DiagnosisEngine diagnosisEngine;
    private final CommandLog commandLog;
    private ApplicationEventPublisher publisher;
    private final ObjectMapper mapper;

    public PatientManager(OrderAccess orderAccess, ApplicationEventPublisher publisher, CommandLog commandLog) {
        this.orderAccess = orderAccess;
        this.diagnosisEngine = new DiagnosisEngine();
        this.commandLog = commandLog;
        this.mapper = new ObjectMapper();
    }

    public ArrayList<Patient> getPatients(){
        return orderAccess.findAllPatients();
    }

    public ArrayList<Observation> getObservations(Integer id){
        return orderAccess.getObservationsByPatient(id);
    }

    // inputs: [ String fullName, Date dob, String note, String staff ]
    public void createPatients(Object[] inputs) throws JsonProcessingException {
        String staff = inputs[3].toString();
        CreatePatientCommand createPatient = new CreatePatientCommand(inputs, orderAccess, staff);
        createPatient.execute();
        commandLog.addCommandLog(CommandType.CreatePatient, mapper.writeValueAsString(createPatient), createPatient.timestamp, createPatient.staff);
    }

    public ArrayList<String> evaluatePatient(Integer id, String staff){
        List<AssociativeFunction> functions = orderAccess.findAllAssociativeFunctions();
        ArrayList<Observation> observations = orderAccess.getObservationsByPatient(id);

        ArrayList<String> inferredObservations = new ArrayList<>();
        for(AssociativeFunction f : functions){
            if(diagnosisEngine.evaluate(staff, f, observations)){
                inferredObservations.add(f.getProductConcept());
            }
        }
        return inferredObservations;
    }
}
