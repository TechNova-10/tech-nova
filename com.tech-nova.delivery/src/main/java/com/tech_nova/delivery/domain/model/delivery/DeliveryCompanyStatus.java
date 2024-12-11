package com.tech_nova.delivery.domain.model.delivery;

public enum DeliveryCompanyStatus {
    COMPANY_MOVING,
    DELIVERY_COMPLETED;

    public static DeliveryCompanyStatus fromString(String status) {
        for (DeliveryCompanyStatus deliveryStatus : DeliveryCompanyStatus.values()) {
            if (deliveryStatus.name().equalsIgnoreCase(status)) {
                return deliveryStatus;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + status);
    }
}