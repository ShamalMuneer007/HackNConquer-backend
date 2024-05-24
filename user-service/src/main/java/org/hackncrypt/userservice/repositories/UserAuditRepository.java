package org.hackncrypt.userservice.repositories;

import org.hackncrypt.userservice.model.entities.User;
import org.hackncrypt.userservice.model.entities.UserAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserAuditRepository extends JpaRepository<UserAudit, Long> {
    UserAudit findByUser(User user);

    List<UserAudit> findByUserUserId(Long userId);
    @Query("SELECT ua FROM UserAudit ua " +
            "WHERE ua.logoutTime < :sevenDaysAgo " +
            "AND ua.logoutTime = (" +
            "    SELECT MAX(ua2.logoutTime) " +
            "    FROM UserAudit ua2 " +
            "    WHERE ua2.user = ua.user " +
            "    AND ua2.logoutTime < :fiveDaysAgo" +
            ")")
    List<UserAudit> findLatestLoggedOutUsersBefore5Days(LocalDateTime sevenDaysAgo);
}
