package com.tech_nova.notificaion.domain.repository;

import com.tech_nova.notificaion.domain.model.Notification;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

}
