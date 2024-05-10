package org.hackncrypt.clanservice.model.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClanDto {
    private String clanName;
    private Integer clanRank;
    private String clanBadgeImageUrl;
}
