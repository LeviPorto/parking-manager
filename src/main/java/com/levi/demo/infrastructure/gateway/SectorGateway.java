package com.levi.demo.infrastructure.gateway;

import com.levi.demo.application.port.out.repository.SectorRepository;
import com.levi.demo.domain.model.Sector;
import com.levi.demo.infrastructure.repository.SectorJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SectorGateway implements SectorRepository {

    private final SectorJpaRepository repository;

    public SectorGateway(SectorJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Sector> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Sector save(Sector sector) {
        return repository.save(sector);
    }
}
