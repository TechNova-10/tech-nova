package com.tech_nova.delivery.domain.model.delivery;

public enum DeliveryStatus {
    HUB_WAITING,
    HUB_MOVING,
    HUB_ARRIVE,
    COMPANY_MOVING,
    DELIVERY_COMPLETED;

    public static DeliveryStatus fromString(String status) {
        for (DeliveryStatus deliveryStatus : DeliveryStatus.values()) {
            if (deliveryStatus.name().equalsIgnoreCase(status)) {
                return deliveryStatus;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + status);
    }
}