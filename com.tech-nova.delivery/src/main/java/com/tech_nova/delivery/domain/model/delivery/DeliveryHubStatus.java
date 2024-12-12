package com.tech_nova.delivery.domain.model.delivery;

public enum DeliveryHubStatus {
    HUB_WAITING,
    HUB_MOVING,
    HUB_ARRIVE;

    public static DeliveryHubStatus fromString(String status) {
        for (DeliveryHubStatus deliveryStatus : DeliveryHubStatus.values()) {
            if (deliveryStatus.name().equalsIgnoreCase(status)) {
                return deliveryStatus;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + status);
    }
}