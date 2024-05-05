package org.hackncrypt.userservice.repositories;

import org.hackncrypt.userservice.model.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    User findByUsername(String username);

    boolean existsByEmail(String email);

    User findByEmail(String email);

    Page<User> findAllByIsDeletedIsFalse(Pageable pageable);
    Page<User> findByIsDeletedFalseOrderByLevelDescXpDesc(Pageable pageable);
    @Query("""
            SELECT ru.rank
            FROM (
                SELECT u.userId, RANK() OVER (ORDER BY u.level DESC, u.xp DESC) AS rank
                FROM User u
                WHERE u.isDeleted = false
            ) ru
            WHERE ru.userId = :userId""")
    Optional<Integer> findUserRankByUserId(@Param("userId") Long userId);

    List<User> findByUsernameStartingWithAndIsBlockedFalse(String username);
}
