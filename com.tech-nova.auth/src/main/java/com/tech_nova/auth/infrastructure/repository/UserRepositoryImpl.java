package com.tech_nova.auth.infrastructure.repository;


import com.tech_nova.auth.domain.model.User;
import com.tech_nova.auth.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepositoryImpl extends JpaRepository<User, UUID>, UserRepository {
}
