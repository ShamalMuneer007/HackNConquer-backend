package org.hackncrypt.userservice.repositories;

import org.hackncrypt.userservice.model.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    User findByUsername(String username);

    boolean existsByEmail(String email);

    User findByEmail(String email);

    Page<User> findAllByIsDeletedIsFalse(Pageable pageable);
}
