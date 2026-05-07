package com.levi.demo.application.usecase;

import com.levi.demo.application.port.out.repository.ParkingSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRevenueImplTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @InjectMocks
    private GetRevenueImpl getRevenue;

    @Test
    void shouldRetrieveRevenueSuccessfully() {
        LocalDate date = LocalDate.of(2026, 5, 5);
        String sector = "A";

        when(parkingSessionRepository.sumRevenueBySectorAndDate(sector, date))
                .thenReturn(new BigDecimal("120.50"));

        BigDecimal amount = getRevenue.retrieve(date, sector);

        assertEquals(new BigDecimal("120.50"), amount);

        verify(parkingSessionRepository)
                .sumRevenueBySectorAndDate(sector, date);
    }
}