package com.project3.project3;

import com.project3.Command.*;

import com.project3.DataTypes.*;
import com.project3.Decorator.ObservationRequest;
import com.project3.Factory.ObservationFactory;
import com.project3.OrderAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RecordObservationCommandTest {

    private ObservationFactory observationFactory;
    private OrderAccess orderAccess;

    @BeforeEach
    void setUp() {
        observationFactory = mock(ObservationFactory.class);
        orderAccess = mock(OrderAccess.class);
    }


    @Test
    void execute_shouldCreateAndPersistMeasurement() throws Exception {

        // ARRANGE
        Object[] inputs = new Object[]{
                "1", "2", "12.5", "kg", "", "staff", "2026-04-14"
        };

        PhenomenonType pt = new PhenomenonType();
        pt.setNormalMin(10.0);
        pt.setNormalMax(20.0);
        List<String> units = new ArrayList<>();
        units.add("kg");
        pt.setAllowedUnits(units);

        Measurement measurement = new Measurement();

        when(observationFactory.createMeasurement(
                any(ObservationRequest.class)
        )).thenReturn(measurement);

        when(orderAccess.findPhenomenonType(2)).thenReturn(pt);

        doAnswer(invocation -> {
            Measurement m = invocation.getArgument(0);
            m.setStatus(ObservationStatus.ACTIVE);
            return null;
        }).when(orderAccess).addMeasurement(any(Measurement.class));

        RecordObservationCommand command =
                new RecordObservationCommand(inputs, observationFactory, orderAccess, "staff", "measurement");

        // ACT
        command.execute();

        // ASSERT
        verify(orderAccess, times(1)).addMeasurement(any(Measurement.class));
    }


    @Test
    void execute_shouldCreateAndPersistCategoryObservation() throws Exception {

        // ARRANGE
        Object[] inputs = new Object[]{
                "1", "2", "present", "3", "2026-04-14", "staff"
        };

        CategoryObservation obs = new CategoryObservation();

        when(observationFactory.createCategoryObservation(
                any(ObservationRequest.class)
        )).thenReturn(obs);

        doAnswer(invocation -> {
            CategoryObservation o = invocation.getArgument(0);
            o.setStatus(ObservationStatus.ACTIVE);
            return null;
        }).when(orderAccess).addCategoryObservation(any(CategoryObservation.class));

        RecordObservationCommand command =
                new RecordObservationCommand(inputs, observationFactory, orderAccess, "staff", "categoryobservation");

        // ACT
        command.execute();

        // ASSERT
        verify(orderAccess, times(1)).addCategoryObservation(any(CategoryObservation.class));
    }


    @Test
    void execute_shouldNotPersist_whenFactoryReturnsNull() throws Exception {

        // ARRANGE
        Object[] inputs = new Object[]{
                "1", "2", "12.5", "kg", "", "staff", "2026-04-14"
        };

        PhenomenonType pt = new PhenomenonType();
        pt.setNormalMin(10.0);
        pt.setNormalMax(20.0);
        List<String> units = new ArrayList<>();
        units.add("kg");
        pt.setAllowedUnits(units);

        when(orderAccess.findPhenomenonType(2)).thenReturn(pt);

        when(observationFactory.createMeasurement(any(ObservationRequest.class)))
                .thenReturn(null);

        RecordObservationCommand command =
                new RecordObservationCommand(inputs, observationFactory, orderAccess, "staff", "measurement");

        // ACT
        command.execute();

        // ASSERT
        verify(orderAccess, never()).addMeasurement(any());
        assertNull(command.observationId);
    }


    @Test
    void execute_shouldHandleCategoryBranchOnly() throws Exception {

        // ARRANGE
        Object[] inputs = new Object[]{
                "1", "2", "absent", "3", "2026-04-14", "staff"
        };

        when(observationFactory.createCategoryObservation(any(ObservationRequest.class)))
                .thenReturn(new CategoryObservation());

        RecordObservationCommand command =
                new RecordObservationCommand(inputs, observationFactory, orderAccess, "staff", "categoryobservation");

        // ACT
        command.execute();

        // ASSERT
        verify(orderAccess).addCategoryObservation(any());
        verify(orderAccess, never()).addMeasurement(any());
    }

    @Test
    void undo_commandSuccess(){
        // ARRANGE
        Object[] inputs = new Object[]{
                102,"252","PRESENT","","2026-04-21","Bucko", 10
        };

        when(observationFactory.createCategoryObservation(any(ObservationRequest.class)))
                .thenReturn(new CategoryObservation());

        RecordObservationCommand command =
                new RecordObservationCommand(inputs, observationFactory, orderAccess, "staff", "categoryobservation");

        // ACT
        command.undo();

        // ASSERT

        assertEquals(command.observationId, 10);
    }
}
