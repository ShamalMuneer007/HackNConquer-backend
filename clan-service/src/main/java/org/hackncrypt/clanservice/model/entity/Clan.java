package org.hackncrypt.clanservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private String clanDescription;
    private Long clanXp;
    @Column(name = "badge_image_url")
    private String clanBadgeImageUrl;
    private String joinClanUrl;
    private Long currentMaxXp = 50L;
    private boolean isDeleted;
    @Column(name = "owner_id",unique = true)
    private Long clanOwnerId;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @ElementCollection
    @CollectionTable(name = "clan_members", joinColumns = @JoinColumn(name = "clan_id"))
    @Column(name = "member_id")
    private Set<Long> members = new HashSet<>();
}
