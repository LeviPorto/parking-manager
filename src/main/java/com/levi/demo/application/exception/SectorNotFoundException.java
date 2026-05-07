package com.levi.demo.application.exception;

public class SectorNotFoundException extends ApplicationException {
    public SectorNotFoundException(Long id) {
        super("Sector '%s' not found".formatted(id));
    }
}
