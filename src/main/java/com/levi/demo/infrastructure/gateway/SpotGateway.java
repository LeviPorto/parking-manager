package com.levi.demo.infrastructure.gateway;

import com.levi.demo.application.port.out.repository.SpotRepository;
import com.levi.demo.domain.model.Spot;
import com.levi.demo.infrastructure.repository.SpotJpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class SpotGateway implements SpotRepository {

    private final SpotJpaRepository repository;

    public SpotGateway(SpotJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Spot> findByExternalId(Long externalId) {
        return repository.findByExternalId(externalId);
    }

    @Override
    public Optional<Spot> findByCoordinatesForUpdate(BigDecimal lat, BigDecimal lng) {
        return repository.findByCoordinatesForUpdate(lat, lng);
    }

    @Override
    public Spot save(Spot spot) {
        return repository.save(spot);
    }
}
