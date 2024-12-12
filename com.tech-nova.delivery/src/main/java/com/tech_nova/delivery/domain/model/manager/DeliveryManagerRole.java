package com.tech_nova.delivery.domain.model.manager;

public enum DeliveryManagerRole {
    HUB_DELIVERY_MANAGER,
    COMPANY_DELIVERY_MANAGER;

    public static DeliveryManagerRole fromString(String role) {
        for (DeliveryManagerRole deliveryManagerRole : DeliveryManagerRole.values()) {
            if (deliveryManagerRole.name().equalsIgnoreCase(role)) {
                return deliveryManagerRole;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
}