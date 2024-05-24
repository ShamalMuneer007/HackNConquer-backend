package org.hackncrypt.clanservice.service;


import org.hackncrypt.clanservice.model.dto.ClanDto;
import org.hackncrypt.clanservice.service.impl.LeaderboardDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ClanService {
    List<ClanDto> searchClansContainingClanName(String name);

    void createClan(String name, String description, long userId, MultipartFile image,String authHeader) throws IOException;

    void joinClan(Long clanId, Long userId,String authHeader);

    List<LeaderboardDto> fetchGlobalClanLeaderboardInfos();

    void leaveClan(Long clanId, long userId, String authHeader);

    void switchOwnership(Long clanId, Long toUserId, long userId, String authHeader);

    ClanDto getClanInfo(Long clanId);

    void disband(Long clanId, long userId, String authHeader);
}
