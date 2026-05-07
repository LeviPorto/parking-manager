package com.levi.demo.infrastructure.repository;

import com.levi.demo.domain.model.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface ParkingSessionJpaRepository extends JpaRepository<ParkingSession, Long> {

    @Query("""
        select p from ParkingSession p
        where p.licensePlate = :licensePlate
        and p.status in ('ENTRY', 'PARKED')
    """)
    Optional<ParkingSession> findActiveByLicensePlate(@Param("licensePlate") String licensePlate);

    @Query(value = """
        select coalesce(sum(p.final_amount), 0)
        from parking_sessions p
        join sectors s on s.id = p.sector_id
        where s.name = :sector
        and p.status = 'EXITED'
        and date(p.exit_time) = :date
    """, nativeQuery = true)
    BigDecimal sumRevenueBySectorAndDate(@Param("sector") String sector,
                                         @Param("date") LocalDate date);
}
