package org.hackncrypt.userservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hackncrypt.userservice.model.entities.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardDto {
    private Long userId;
    private String username;
    private int level;
    private int xp;
    private String profileImage;
    private String email;
    private Integer playerRank;
    public LeaderboardDto(User user){
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.level = user.getLevel();
        this.profileImage = user.getProfileImageUrl();
        this.xp = user.getXp();
        this.userId = user.getUserId();
        this.playerRank = user.getPlayerRank();
    }
}
