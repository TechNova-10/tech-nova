package com.tech_nova.auth.domain.model;

public enum UserRole {
    MASTER,
    HUB_MANAGER,
    COMPANY_MANAGER,
    HUB_DELIVERY_MANAGER,
    COMPANY_DELIVERY_MANAGER;

    public static UserRole fromString(String role) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
}