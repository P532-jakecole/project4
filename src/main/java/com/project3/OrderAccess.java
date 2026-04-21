package com.project3;

import com.project3.DataTypes.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;

import java.util.*;

@Repository
@Transactional
public class OrderAccess {

    @PersistenceContext
    private EntityManager entityManager;

    public void addPatient(Patient patient) {
        entityManager.persist(patient);
    }

    public Patient findPatient(Integer id) {
        return entityManager.find(Patient.class, id);
    }

    public ArrayList<Patient> findAllPatients() {
        return (ArrayList<Patient>) entityManager
                .createQuery("from Patient", Patient.class)
                .getResultList();
    }

    public void updatePatientNote(Integer patientId, String note) {
        Patient p = findPatient(patientId);
        if (p != null) {
            p.setNote(note);
            entityManager.merge(p);
        }
    }

    public void addPhenomenonType(PhenomenonType pt){
        entityManager.persist(pt);
    }

    public PhenomenonType findPhenomenonType(Integer id) {
        return entityManager.find(PhenomenonType.class, id);
    }

    public ArrayList<PhenomenonType> findAllPhenomenonTypes() {
        return (ArrayList<PhenomenonType>) entityManager
                .createQuery("from PhenomenonType", PhenomenonType.class)
                .getResultList();
    }

    public ArrayList<Phenomenon> getPhenomenon() {
        return (ArrayList<Phenomenon>) entityManager
                .createQuery("from Phenomenon ", Phenomenon.class)
                .getResultList();
    }

    public void updatePhenomenonTypeName(Integer id, String name) {
        PhenomenonType pt = findPhenomenonType(id);
        if (pt != null) {
            pt.setName(name);
            entityManager.merge(pt);
        }
    }

    public Phenomenon createPhenomenon(String name, Integer typeId) {
        PhenomenonType pt = findPhenomenonType(typeId);

        Phenomenon p = new Phenomenon();
        p.setName(name);
        p.setPhenomenonType(pt);

        entityManager.persist(p);
        return p;
    }

    public void addPhenomenon(Phenomenon phenomenon){
        entityManager.persist(phenomenon);
    }

    public ArrayList<Phenomenon> findPhenomenaByType(Integer typeId) {
        return (ArrayList<Phenomenon>) entityManager
                .createQuery(
                        "from Phenomenon p where p.phenomenonType.id = :id",
                        Phenomenon.class)
                .setParameter("id", typeId)
                .getResultList();
    }

    public Phenomenon getPhenomenaById(Integer id) {
        return entityManager.find(Phenomenon.class, id);
    }

    public void addProtocol(Protocol p){
        entityManager.persist(p);
    }

    public Protocol findProtocol(Integer id) {
        return entityManager.find(Protocol.class, id);
    }

    public ArrayList<Protocol> findAllProtocols() {
        return (ArrayList<Protocol>) entityManager
                .createQuery("from Protocol", Protocol.class)
                .getResultList();
    }

    public void updateProtocolRating(Integer id, AccuracyRating rating) {
        Protocol p = findProtocol(id);
        if (p != null) {
            p.setAccuracyRating(rating);
            entityManager.merge(p);
        }
    }

    public void addMeasurement(Measurement measurement){
        entityManager.persist(measurement);
    }

    public void addCategoryObservation(CategoryObservation observation){
        entityManager.persist(observation);
    }

    public ArrayList<Observation> getObservationsByPatient(Integer patientId) {
        return (ArrayList<Observation>) entityManager
                .createQuery(
                        "from Observation o where o.patient.id = :pid order by o.recordingTime desc",
                        Observation.class)
                .setParameter("pid", patientId)
                .getResultList();
    }

    public Observation rejectObservation(Integer observationId) {
        Observation obs = entityManager.find(Observation.class, observationId);
        if (obs != null) {
            obs.setStatus(ObservationStatus.REJECTED);
            entityManager.merge(obs);
            return obs;
        }
        return null;
    }

    public void setActiveObservationStatus(Integer observationId) {
        Observation obs = entityManager.find(Observation.class, observationId);
        if (obs != null) {
            obs.setStatus(ObservationStatus.ACTIVE);
            entityManager.merge(obs);
        }
    }

    public AssociativeFunction createAssociativeFunction(String name, List<ArgumentWeight> args, String product, String strategy) {
        AssociativeFunction f = new AssociativeFunction();
        f.setName(name);
        f.setArgumentConcepts(args);
        f.setProductConcept(product);
        switch (strategy.toLowerCase()){
            case "conjunctive":
                f.setStrategy(Strategy.CONJUNCTIVE);
                break;
            default:
                f.setStrategy(Strategy.WEIGHTED);
        }

        entityManager.persist(f);
        return f;
    }

    public Observation getObservation(Integer observationId) {
        return entityManager.find(Observation.class, observationId);
    }

    public boolean existsObservation(Integer patientId, Integer phenomenonId, Presence presence) {
        return !entityManager.createQuery(
                        "from CategoryObservation o where o.patient.id = :pid and o.phenomenon.id = :phid and o.presence = :p and o.status = 'ACTIVE'",
                        CategoryObservation.class)
                .setParameter("pid", patientId)
                .setParameter("phid", phenomenonId)
                .setParameter("p", presence)
                .getResultList()
                .isEmpty();
    }

    public List<Phenomenon> findChildren(Integer parentId) {
        return entityManager.createQuery(
                        "from Phenomenon p where p.parentConcept.id = :id",
                        Phenomenon.class)
                .setParameter("id", parentId)
                .getResultList();
    }

    public void addAssociativeFunction(AssociativeFunction af){
        entityManager.persist(af);
    }

    public ArrayList<AssociativeFunction> findAllAssociativeFunctions() {
        return (ArrayList<AssociativeFunction>) entityManager
                .createQuery("from AssociativeFunction", AssociativeFunction.class)
                .getResultList();
    }

    public AuditLogEntry recordAudit(String event, Integer observationId, Integer patientId) {
        AuditLogEntry entry = new AuditLogEntry();
        entry.setEvent(event);
        entry.setObservationId(observationId);
        entry.setPatientId(patientId);
        entry.setTimestamp(new Date());

        entityManager.persist(entry);
        return entry;
    }

    public void addUser(User user) {
        entityManager.persist(user);
    }

    public User getUser(Integer id) {
        return entityManager.find(User.class, id);
    }

    public User getUserByUsername(String username) {
        return entityManager.createQuery(
                        "from User u where u.username = :username",
                        User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public ArrayList<User> getUsers() {
        return (ArrayList<User>) entityManager
                .createQuery("from User", User.class)
                .getResultList();
    }

    public void flush(){
        entityManager.flush();
    }
}
