package org.hackncrypt.clanservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.clanservice.model.dto.ClanDto;
import org.hackncrypt.clanservice.service.ClanService;
import org.hackncrypt.clanservice.service.impl.LeaderboardDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PublicController {
    private final ClanService clanService;
    @GetMapping("/get-info/{clanId}")
    public ResponseEntity<ClanDto> getClanInfo(@PathVariable Long clanId) {
        return ResponseEntity.ok(clanService.getClanInfo(clanId));
    }
    @GetMapping("/get-clan-leaderboard")
    public ResponseEntity<List<LeaderboardDto>> getClanLeaderboard(){
        return ResponseEntity.ok(clanService.fetchGlobalClanLeaderboardInfos());
    }
    @GetMapping("/search")
    public ResponseEntity<List<ClanDto>> searchUsersLikeUsername(@RequestParam("name") String name){
        return ResponseEntity.ok(clanService.searchClansContainingClanName(name));
    }
}
