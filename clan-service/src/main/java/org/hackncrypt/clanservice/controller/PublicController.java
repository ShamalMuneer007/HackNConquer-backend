package org.hackncrypt.clanservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.clanservice.model.dto.ClanDto;
import org.hackncrypt.clanservice.service.ClanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PublicController {
    private final ClanService clanService;
    @GetMapping("/search")
    public ResponseEntity<List<ClanDto>> searchUsersLikeUsername(@RequestParam("name") String name){
        return ResponseEntity.ok(clanService.searchUsersContainingClanName(name));
    }
}
