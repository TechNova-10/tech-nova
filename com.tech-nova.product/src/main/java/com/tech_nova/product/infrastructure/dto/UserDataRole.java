package com.tech_nova.product.infrastructure.dto;

public enum UserDataRole {
    MASTER,
    HUB_MANAGER,
    COMPANY_MANAGER,
    HUB_DELIVERY_MANAGER,
    COMPANY_DELIVERY_MANAGER;

    public static UserDataRole fromString(String role) {
        for (UserDataRole userDataRole : UserDataRole.values()) {
            if (userDataRole.name().equalsIgnoreCase(role)) {
                return userDataRole;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
}