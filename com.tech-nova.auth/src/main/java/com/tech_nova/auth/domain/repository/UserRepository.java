package com.tech_nova.auth.domain.repository;

import com.tech_nova.auth.domain.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository {
    Optional<User> findById(UUID id);

    Optional<User> findByIdAndIsDeletedFalse(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    User save(User delivery);
}
