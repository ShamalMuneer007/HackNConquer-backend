package org.hackncrypt.userservice.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hackncrypt.userservice.enums.FriendStatus;
import org.hackncrypt.userservice.enums.Role;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;
    private String username;
    private String password;
    private Integer playerRank;
    private String phoneNumber;
    private String profileImageUrl;
    private String email;
    private String deviceToken;
    private int level = 1;
    private int currentMaxXp = 50;
    private int xp = 0;
    private boolean isPremium;
    private boolean isBlocked;
    private LocalDateTime created_at;
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends;
    private boolean isDeleted;
}
