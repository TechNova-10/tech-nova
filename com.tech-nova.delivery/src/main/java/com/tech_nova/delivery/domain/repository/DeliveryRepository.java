package com.tech_nova.delivery.domain.repository;

import com.tech_nova.delivery.domain.model.delivery.Delivery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {
    List<Delivery> findAll();

    Optional<Delivery> findById(UUID id);

    boolean existsByOrderId(UUID orderId);

    Delivery save(Delivery delivery);
}