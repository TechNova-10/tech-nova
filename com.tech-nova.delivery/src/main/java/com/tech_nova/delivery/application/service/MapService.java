package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.application.dto.LocationData;

public interface MapService {
    LocationData getCoordinates(String address);
}