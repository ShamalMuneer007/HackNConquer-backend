package org.hackncrypt.clanservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.clanservice.model.dto.ClanDto;
import org.hackncrypt.clanservice.model.entity.Clan;
import org.hackncrypt.clanservice.repository.ClanRepository;
import org.hackncrypt.clanservice.service.ClanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClanServiceImpl implements ClanService {
    private final ClanRepository clanRepository;
    @Override
    public List<ClanDto> searchUsersContainingClanName(String name) {
        List<Clan> clanList = clanRepository.findByClanNameStartingWithAndIsDeletedFalse(name);
        return null;
    }
}
