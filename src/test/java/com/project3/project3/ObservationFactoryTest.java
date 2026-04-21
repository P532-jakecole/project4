package com.project3.project3;

import com.project3.DataTypes.*;
import com.project3.Decorator.ObservationRequest;
import com.project3.Factory.ObservationFactory;
import com.project3.OrderAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservationFactoryTest {

    @Mock
    private OrderAccess orderAccess;

    @InjectMocks
    private ObservationFactory observationFactory;

    private Patient patient;
    private PhenomenonType quantitativeType;
    private PhenomenonType qualitativeType;
    private Protocol protocol;
    private Phenomenon phenomenon;

    @BeforeEach
    void setUp() {
        patient = new Patient();

        quantitativeType = new PhenomenonType();
        quantitativeType.setKind(PhenomenonKind.QUANTITATIVE);
        quantitativeType.setAllowedUnits(Arrays.asList("kg", "cm"));

        qualitativeType = new PhenomenonType();
        qualitativeType.setKind(PhenomenonKind.QUALITATIVE);

        phenomenon = new Phenomenon();

        qualitativeType.setPhenomena(List.of(phenomenon));
        phenomenon.setPhenomenonType(qualitativeType);

        protocol = new Protocol();
    }

    @Test
    void createMeasurement_valid_shouldReturnMeasurement() {
        // Arrange
        when(orderAccess.findPatient(1)).thenReturn(patient);
        when(orderAccess.findPhenomenonType(1)).thenReturn(quantitativeType);
        when(orderAccess.findProtocol(1)).thenReturn(protocol);
        ObservationRequest request = new ObservationRequest(1, 1, "measurement", orderAccess, new Date());
        request.setPhenomenonTypeId(1);
        request.setAmount(70.0);
        request.setUnit("kg");

        // Act
        Measurement m = observationFactory.createMeasurement(request);

        // Assert
        assertNotNull(m);
        assertEquals(patient, m.getPatient());
        assertEquals(70.0, m.getAmount());
        assertEquals("kg", m.getUnit());
        assertEquals(protocol, m.getProtocol());
    }

    @Test
    void createMeasurement_nullApplicability_shouldUseNow() {
        // Arrange
        when(orderAccess.findPatient(1)).thenReturn(patient);
        when(orderAccess.findPhenomenonType(1)).thenReturn(quantitativeType);
        ObservationRequest request = new ObservationRequest(1, 0, "measurement", orderAccess, null);
        request.setPhenomenonTypeId(1);
        request.setAmount(70.0);
        request.setUnit("kg");

        // Act
        Measurement m = observationFactory.createMeasurement(request);

        // Assert
        assertNotNull(m);
        assertNotNull(m.getApplicabilityTime());
    }

    @Test
    void createCategoryObservation_valid_shouldReturnObservation() {
        // Arrange
        when(orderAccess.findPatient(1)).thenReturn(patient);
        when(orderAccess.getPhenomenaById(1)).thenReturn(phenomenon);
        when(orderAccess.findProtocol(1)).thenReturn(protocol);
        ObservationRequest request = new ObservationRequest(1, 1, "categoryobservation", orderAccess, new Date());
        request.setPhenomenonId(1);
        request.setPresence(Presence.PRESENT);

        // Act
        CategoryObservation obs = observationFactory.createCategoryObservation(
                request
        );

        // Assert
        assertNotNull(obs);
        assertEquals(patient, obs.getPatient());
        assertEquals(phenomenon, obs.getPhenomenon());
        assertEquals(Presence.PRESENT, obs.getPresence());
        assertEquals(protocol, obs.getProtocol());
    }

    @Test
    void createCategoryObservation_nullApplicability_shouldUseNow() {
        // Arrange
        when(orderAccess.findPatient(1)).thenReturn(patient);
        when(orderAccess.getPhenomenaById(1)).thenReturn(phenomenon);
        ObservationRequest request = new ObservationRequest(1, null, "categoryobservation", orderAccess, null);
        request.setPhenomenonId(1);
        request.setPresence(Presence.ABSENT);

        // Act
        CategoryObservation obs = observationFactory.createCategoryObservation(
                request
        );

        // Assert
        assertNotNull(obs);
        assertNotNull(obs.getApplicabilityTime());
    }
}
