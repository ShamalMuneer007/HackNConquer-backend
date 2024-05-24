package org.hackncrypt.userservice.repositories;

import org.hackncrypt.userservice.model.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    @Modifying
    @Transactional
    @Query("UPDATE User u " +
            "SET u.playerRank = (" +
            "    SELECT ru.player_rank " +
            "    FROM (" +
            "        SELECT u2.userId AS userId, RANK() OVER (ORDER BY u2.level DESC, u2.xp DESC) AS player_rank " +
            "        FROM User u2 " +
            "        WHERE u2.isDeleted = false" +
            "    ) AS ru " +
            "    WHERE ru.userId = u.userId" +
            ")")
    void updateAllUsersRank();

        List<User> findByUsernameStartingWithAndIsBlockedFalse(String username);

    List<User> findAllByClan(Long clanId);
}
