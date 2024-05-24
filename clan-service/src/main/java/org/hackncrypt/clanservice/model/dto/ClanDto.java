package org.hackncrypt.clanservice.model.dto;


import lombok.*;
import org.hackncrypt.clanservice.model.entity.Clan;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClanDto {
    private String clanName;
    private Long clanId;
    private Integer clanRank;
    private String clanBadgeImageUrl;
    private Long owner;
    private Set<Long> members;
    private Long xp;
    private Integer level;
    private String createdAt;
    private String description;
    private String joinClanUrl;
    public ClanDto(Clan clan) {
        this.clanName = clan.getClanName();
        this.clanId = clan.getClanId();
        this.clanBadgeImageUrl = clan.getClanBadgeImageUrl();
        this.clanRank = clan.getClanRank();
        this.owner = clan.getClanOwnerId();
        this.members = clan.getMembers();
        this.xp= clan.getClanXp();
        this.level = clan.getClanLevel();
        this.createdAt = clan.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.description = clan.getClanDescription();
        this.joinClanUrl = clan.getJoinClanUrl();
    }
}
