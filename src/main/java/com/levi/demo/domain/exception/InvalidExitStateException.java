package com.levi.demo.domain.exception;

public class InvalidExitStateException extends DomainException {

    public InvalidExitStateException(String currentStatus) {
        super("Cannot exit vehicle when session is in status: " + currentStatus);
    }
}