package com.levi.demo.domain.exception;

public class InvalidParkingStateException extends DomainException {

    public InvalidParkingStateException(String currentStatus) {
        super("Cannot park vehicle when session is in status: " + currentStatus);
    }
}