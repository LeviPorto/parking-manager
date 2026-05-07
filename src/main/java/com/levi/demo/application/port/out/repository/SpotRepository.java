package com.levi.demo.application.port.out.repository;

import com.levi.demo.domain.model.Spot;

import java.math.BigDecimal;
import java.util.Optional;

public interface SpotRepository {
    Optional<Spot> findByExternalId(Long externalId);
    Optional<Spot> findByCoordinatesForUpdate(BigDecimal lat, BigDecimal lng);
    Spot save(Spot spot);
}
