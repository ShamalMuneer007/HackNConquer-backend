package org.hackncrypt.userservice.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hackncrypt.userservice.enums.FriendStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver")
    private User receiver;


    @CreationTimestamp
    private LocalDateTime createdAt;
}