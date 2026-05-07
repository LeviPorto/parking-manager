package com.levi.demo.application.port.out.repository;

import com.levi.demo.domain.model.Sector;

import java.util.Optional;

public interface SectorRepository {
    Optional<Sector> findByName(String name);
    Optional<Sector> findByIdForUpdate(Long id);
    Sector save(Sector sector);
}
