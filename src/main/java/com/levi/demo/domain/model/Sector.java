package com.levi.demo.domain.model;

import com.levi.demo.domain.exception.SectorFullException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Entity
@Table(name = "sectors")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private Integer maxCapacity;

    @Column(nullable = false)
    private Integer occupiedCount = 0;

    public Sector(String name, BigDecimal basePrice, Integer maxCapacity) {
        this.name = name;
        this.basePrice = basePrice;
        this.maxCapacity = maxCapacity;
    }

    public boolean isFull() {
        return occupiedCount >= maxCapacity;
    }

    public BigDecimal getOccupancyRate() {
        if (maxCapacity == null || maxCapacity == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(occupiedCount)
                .divide(BigDecimal.valueOf(maxCapacity), 4, RoundingMode.HALF_UP);
    }

    public void reserveSpot() {
        if (isFull()) {
            throw new SectorFullException(name);
        }
        occupiedCount++;
    }

    public void releaseSpot() {
        if (occupiedCount > 0) {
            occupiedCount--;
        }
    }
}
