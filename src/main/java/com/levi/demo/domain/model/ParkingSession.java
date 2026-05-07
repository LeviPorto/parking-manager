package com.levi.demo.domain.model;

import com.levi.demo.domain.enums.ParkingSessionStatus;
import com.levi.demo.domain.exception.InvalidExitStateException;
import com.levi.demo.domain.exception.InvalidParkingStateException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "parking_sessions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParkingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String licensePlate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id")
    private Spot spot;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @Column(precision = 10, scale = 2)
    private BigDecimal entryBasePrice;

    @Column(precision = 5, scale = 2)
    private BigDecimal occupancyMultiplier;

    @Column(precision = 10, scale = 2)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParkingSessionStatus status;

    public ParkingSession(String licensePlate, LocalDateTime entryTime,
                          ParkingSessionStatus status) {
        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.status = status;
    }

    public static ParkingSession start(String licensePlate, LocalDateTime entryTime) {
        return new ParkingSession(licensePlate, entryTime,
                ParkingSessionStatus.ENTRY);
    }

    public void park(Spot spot, BigDecimal occupancyMultiplier) {
        if (this.status != ParkingSessionStatus.ENTRY) {
            throw new InvalidParkingStateException(this.status.toString());
        }

        this.spot = spot;
        this.sector = spot.getSector();
        this.entryBasePrice = spot.getSector().getBasePrice();
        this.occupancyMultiplier = occupancyMultiplier;
        this.status = ParkingSessionStatus.PARKED;
    }

    public void finish(LocalDateTime exitTime, BigDecimal amount) {
        if (this.status != ParkingSessionStatus.PARKED) {
            throw new InvalidExitStateException(this.status.toString());
        }

        this.exitTime = exitTime;
        this.finalAmount = amount;
        this.status = ParkingSessionStatus.EXITED;
    }

}
