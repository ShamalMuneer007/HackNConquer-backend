package org.hackncrypt.clanservice.service;


import org.hackncrypt.clanservice.model.dto.ClanDto;

import java.util.List;

public interface ClanService {
    List<ClanDto> searchUsersContainingClanName(String name);
}
