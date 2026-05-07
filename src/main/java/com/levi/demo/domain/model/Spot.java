package com.levi.demo.domain.model;

import com.levi.demo.domain.exception.SpotAlreadyOccupiedException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "spots")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long externalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal lat;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal lng;

    @Column(nullable = false)
    private Boolean occupied = false;

    private String currentLicensePlate;

    public Spot(Long externalId, Sector sector, BigDecimal lat, BigDecimal lng) {
        this.externalId = externalId;
        this.sector = sector;
        this.lat = lat;
        this.lng = lng;
    }

    public void occupy(String licensePlate) {
        if (Boolean.TRUE.equals(occupied)) {
            throw new SpotAlreadyOccupiedException(id);
        }
        occupied = true;
        currentLicensePlate = licensePlate;
    }

    public void release() {
        occupied = false;
        currentLicensePlate = null;
    }
}
