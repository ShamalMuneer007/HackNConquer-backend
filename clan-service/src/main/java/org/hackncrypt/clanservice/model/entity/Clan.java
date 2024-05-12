package org.hackncrypt.clanservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Clan {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "clan_id")
    private Long clanId;
    private String clanName;
    private Integer clanRank;
    private Integer clanLevel;
    private Long clanXp;
    @Column(name = "badge_image_url")
    private String clanBadgeImageUrl;
    private boolean isDeleted;
    @Column(name = "owner_id")
    private Long clanOwnerId;
    @ElementCollection
    @CollectionTable(name = "clan_members", joinColumns = @JoinColumn(name = "clan_id"))
    @Column(name = "member_id")
    private List<Long> members = new ArrayList<>();
}
