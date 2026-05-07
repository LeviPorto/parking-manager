package com.levi.demo.application.usecase;

import com.levi.demo.application.exception.ActiveParkingSessionNotFoundException;
import com.levi.demo.application.exception.SectorNotFoundException;
import com.levi.demo.application.exception.SpotNotFoundException;
import com.levi.demo.application.port.out.repository.ParkingSessionRepository;
import com.levi.demo.application.port.out.repository.SectorRepository;
import com.levi.demo.application.port.out.repository.SpotRepository;
import com.levi.demo.application.service.PricingService;
import com.levi.demo.application.usecase.command.ParkingEventCommand;
import com.levi.demo.domain.enums.ParkingEventType;
import com.levi.demo.domain.enums.ParkingSessionStatus;
import com.levi.demo.domain.model.ParkingSession;
import com.levi.demo.domain.model.Sector;
import com.levi.demo.domain.model.Spot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessParkingEventImplTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private ProcessParkingEventImpl processParkingEvent;

    @Test
    void shouldProcessEntrySuccessfully() {
        ParkingEventCommand command = new ParkingEventCommand(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0),
                null,
                null,
                null,
                ParkingEventType.ENTRY
        );

        when(parkingSessionRepository.findActiveByLicensePlate("ABC1234"))
                .thenReturn(Optional.empty());

        processParkingEvent.execute(command);

        verify(parkingSessionRepository).save(argThat(session ->
                session.getLicensePlate().equals("ABC1234")
                        && session.getStatus() == ParkingSessionStatus.ENTRY
        ));
    }

    @Test
    void shouldIgnoreDuplicatedEntry() {
        ParkingSession existingSession = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        ParkingEventCommand command = new ParkingEventCommand(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0),
                null,
                null,
                null,
                ParkingEventType.ENTRY
        );

        when(parkingSessionRepository.findActiveByLicensePlate("ABC1234"))
                .thenReturn(Optional.of(existingSession));

        processParkingEvent.execute(command);

        verify(parkingSessionRepository, never()).save(any(ParkingSession.class));
    }

    @Test
    void shouldProcessParkSuccessfully() {
        Sector sector = new Sector("A", new BigDecimal("40.50"), 10);

        Spot spot = new Spot(
                1L,
                sector,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981")
        );

        ParkingSession session = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        ParkingEventCommand command = new ParkingEventCommand(
                "ABC1234",
                null,
                null,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981"),
                ParkingEventType.PARKED
        );

        when(parkingSessionRepository.findActiveByLicensePlate("ABC1234"))
                .thenReturn(Optional.of(session));

        when(spotRepository.findByCoordinatesForUpdate(command.lat(), command.lng()))
                .thenReturn(Optional.of(spot));

        when(sectorRepository.findByIdForUpdate(spot.getSector().getId()))
                .thenReturn(Optional.of(sector));

        when(pricingService.calculateOccupancyMultiplier(sector))
                .thenReturn(new BigDecimal("1.00"));

        processParkingEvent.execute(command);

        assertTrue(spot.getOccupied());
        assertEquals("ABC1234", spot.getCurrentLicensePlate());
        assertEquals(1, sector.getOccupiedCount());
        assertEquals(ParkingSessionStatus.PARKED, session.getStatus());

        verify(sectorRepository).save(sector);
        verify(spotRepository).save(spot);
        verify(parkingSessionRepository).save(session);
    }

    @Test
    void shouldThrowExceptionWhenParkWithoutActiveSession() {
        ParkingEventCommand command = new ParkingEventCommand(
                "ABC1234",
                null,
                null,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981"),
                ParkingEventType.PARKED
        );

        when(parkingSessionRepository.findActiveByLicensePlate("ABC1234"))
                .thenReturn(Optional.empty());

        assertThrows(
                ActiveParkingSessionNotFoundException.class,
                () -> processParkingEvent.execute(command)
        );

        verifyNoInteractions(spotRepository, sectorRepository, pricingService);
    }

    @Test
    void shouldThrowExceptionWhenSpotNotFoundOnPark() {
        ParkingSession session = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        ParkingEventCommand command = new ParkingEventCommand(
                "ABC1234",
                null,
                null,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981"),
                ParkingEventType.PARKED
        );

        when(parkingSessionRepository.findActiveByLicensePlate("ABC1234"))
                .thenReturn(Optional.of(session));

        when(spotRepository.findByCoordinatesForUpdate(command.lat(), command.lng()))
                .thenReturn(Optional.empty());

        assertThrows(
                SpotNotFoundException.class,
                () -> processParkingEvent.execute(command)
        );

        verify(sectorRepository, never()).findByIdForUpdate(any());
        verify(parkingSessionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSectorNotFoundOnPark() {
        Sector sector = new Sector("A", new BigDecimal("40.50"), 10);

        Spot spot = new Spot(
                1L,
                sector,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981")
        );

        ParkingSession session = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        ParkingEventCommand command = new ParkingEventCommand(
                "ABC1234",
                null,
                null,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981"),
                ParkingEventType.PARKED
        );

        when(parkingSessionRepository.findActiveByLicensePlate("ABC1234"))
                .thenReturn(Optional.of(session));

        when(spotRepository.findByCoordinatesForUpdate(command.lat(), command.lng()))
                .thenReturn(Optional.of(spot));

        when(sectorRepository.findByIdForUpdate(spot.getSector().getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                SectorNotFoundException.class,
                () -> processParkingEvent.execute(command)
        );

        verify(parkingSessionRepository, never()).save(any());
        verify(spotRepository, never()).save(any());
    }

    @Test
    void shouldProcessExitSuccessfully() {
        Sector sector = new Sector("A", new BigDecimal("40.50"), 10);

        Spot spot = new Spot(
                1L,
                sector,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981")
        );

        ParkingSession session = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        sector.reserveSpot();
        spot.occupy("ABC1234");
        session.park(spot, new BigDecimal("1.00"));

        ParkingEventCommand command = new ParkingEventCommand(
                "ABC1234",
                null,
                LocalDateTime.of(2026, 5, 5, 11, 0),
                null,
                null,
                ParkingEventType.EXIT
        );

        when(parkingSessionRepository.findActiveByLicensePlate("ABC1234"))
                .thenReturn(Optional.of(session));

        when(pricingService.calculateOccupancyAmount(session, command.exitTime()))
                .thenReturn(new BigDecimal("40.50"));

        processParkingEvent.execute(command);

        assertFalse(spot.getOccupied());
        assertNull(spot.getCurrentLicensePlate());
        assertEquals(0, sector.getOccupiedCount());
        assertEquals(ParkingSessionStatus.EXITED, session.getStatus());
        assertEquals(new BigDecimal("40.50"), session.getFinalAmount());

        verify(spotRepository).save(spot);
        verify(sectorRepository).save(sector);
        verify(parkingSessionRepository).save(session);
    }

    @Test
    void shouldThrowExceptionWhenExitWithoutActiveSession() {
        ParkingEventCommand command = new ParkingEventCommand(
                "ABC1234",
                null,
                LocalDateTime.of(2026, 5, 5, 11, 0),
                null,
                null,
                ParkingEventType.EXIT
        );

        when(parkingSessionRepository.findActiveByLicensePlate("ABC1234"))
                .thenReturn(Optional.empty());

        assertThrows(
                ActiveParkingSessionNotFoundException.class,
                () -> processParkingEvent.execute(command)
        );

        verify(pricingService, never()).calculateOccupancyAmount(any(), any());
        verify(parkingSessionRepository, never()).save(any());
    }
}