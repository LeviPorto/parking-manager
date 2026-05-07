package com.levi.demo.domain.exception;

public class SectorFullException extends DomainException {
    public SectorFullException(String sectorName) {
        super("Sector '%s' is full".formatted(sectorName));
    }
}
