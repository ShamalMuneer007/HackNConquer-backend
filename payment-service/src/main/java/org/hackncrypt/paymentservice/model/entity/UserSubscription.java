package org.hackncrypt.paymentservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userSubscriptionId;
    private String subscriptionId;
    private Long userId;
    private String customerId;
    @CreationTimestamp
    private LocalDateTime subscribedAt;
    @UpdateTimestamp
    private LocalDateTime subscriptionUpdatedAt;
    private LocalDateTime subscriptionCancelledAt;
    private boolean subscriptionCancelled;
}
