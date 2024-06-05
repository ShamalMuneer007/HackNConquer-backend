package org.hackncrypt.paymentservice.repository;

import org.apache.catalina.User;
import org.hackncrypt.paymentservice.model.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription,Long> {
    Optional<UserSubscription> findBySubscriptionId(String id);

    List<UserSubscription> findByUserId(Long userId);

    Long countBySubscriptionCancelledFalse();
}
