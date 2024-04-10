package org.hackncrypt.userservice.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hackncrypt.userservice.enums.Role;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    private String username;
    private String password;
    private String playerRank;
    private String phoneNumber;
    private String profileImageUrl;
    private String email;
    private int level = 1;
    private int currentMaxXp = 50;
    private int xp = 0;
    private boolean isPremium;
    private boolean isBlocked;
    private Date created_at;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean isDeleted;
}
