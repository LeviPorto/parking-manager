package com.levi.demo.infrastructure.repository;

import com.levi.demo.domain.model.Spot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface SpotJpaRepository extends JpaRepository<Spot, Long> {
    Optional<Spot> findByExternalId(Long externalId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Spot s where s.lat = :lat and s.lng = :lng")
    Optional<Spot> findByCoordinatesForUpdate(@Param("lat") BigDecimal lat,
                                              @Param("lng") BigDecimal lng);
}
