package com.project3;


import com.project3.DataTypes.*;
import com.project3.Managers.ObservationManager;
import com.project3.Managers.PatientManager;
import org.springframework.web.bind.annotation.*;
import com.project3.Managers.ProtocolManager;

import java.util.ArrayList;

@RestController
@CrossOrigin(
        origins = "https://p532-jakecole.github.io",
        allowedHeaders = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
@RequestMapping("/api")
public class BusinessClient {

    private final ObservationManager observationManager;
    private final PatientManager patientManager;
    private final ProtocolManager protocolManager;
    private final CommandLog commandLog;

    public BusinessClient(ObservationManager observationManager, PatientManager patientManager, ProtocolManager protocolManager, CommandLog commandLog) {
        this.observationManager = observationManager;
        this.patientManager = patientManager;
        this.protocolManager = protocolManager;
        this.commandLog = commandLog;
    }

    @GetMapping("/patients")
    public ArrayList<Patient> getPatients() {
        try{
            return patientManager.getPatients();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/patients/{id}/observations")
    public ArrayList<Observation> getObservations(@PathVariable int id) {
        try{
            return patientManager.getObservations(id);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/patients")
    public void createPatients(@RequestBody Object[] patient) {
        try{
            patientManager.createPatients(patient);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/patients/{id}/evaluate")
    public ArrayList<String> evaluatePatient(@PathVariable Integer id, @RequestBody String staff) {
        try{
            return patientManager.evaluatePatient(id, staff);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/observations/measurement")
    public void recordMeasurement(@RequestBody Object[] measurement) {
        try{
            observationManager.recordMeasurement(measurement);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/observations/category")
    public void recordCategoryObservation(@RequestBody Object[] categoryObservation) {
        try{
            observationManager.recordCategoryObservation(categoryObservation);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/observations/{id}/reject")
    public void rejectObservation(@PathVariable Integer id, @RequestBody String[] reasonStaff) {
        try{
            observationManager.rejectObservation(id, reasonStaff[0], reasonStaff[1]);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/phenomenon-types")
    public ArrayList<PhenomenonType> listPhenomenonTypes() {
        try{
            return observationManager.listPhenomenonTypes();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/phenomenon-types")
    public void createPhenomenonType(@RequestBody Object[] type) {
        try{
            observationManager.createPhenomenonType(type);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/protocols")
    public ArrayList<Protocol> listProtocols() {
        try{
            return protocolManager.listProtocols();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/protocols")
    public void createProtocol(@RequestBody Object[] protocol) {
        try{
            protocolManager.createProtocol(protocol);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/command-log")
    public ArrayList<CommandLogEntry> viewCommandLog() {
        try{
            return commandLog.getCommandLog();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/audit-log")
    public ArrayList<AuditLogEntry> viewAuditLog() {
        try{
            return commandLog.getAuditLog();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
