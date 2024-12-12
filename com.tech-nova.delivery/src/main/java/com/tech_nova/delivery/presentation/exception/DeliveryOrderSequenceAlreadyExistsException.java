package com.tech_nova.delivery.presentation.exception;

public class DeliveryOrderSequenceAlreadyExistsException extends RuntimeException {
    public DeliveryOrderSequenceAlreadyExistsException(String message) {
        super(message);
    }
}