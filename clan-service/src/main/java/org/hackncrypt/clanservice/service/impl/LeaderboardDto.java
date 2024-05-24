package org.hackncrypt.clanservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hackncrypt.clanservice.model.entity.Clan;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardDto {
    private Long clanId;
    private String clanName;
    private int clanLevel;
    private Long clanXp;
    private String clanBadgeImageUrl;
    private int members;
    private Integer clanRank;
    public LeaderboardDto(Clan clan){
        this.clanName = clan.getClanName();
        this.clanLevel = clan.getClanLevel();
        this.clanBadgeImageUrl = clan.getClanBadgeImageUrl();
        this.clanXp = clan.getClanXp();
        this.clanId = clan.getClanId();
        this.clanRank = clan.getClanRank();
        this.members = clan.getMembers().size();
    }
}