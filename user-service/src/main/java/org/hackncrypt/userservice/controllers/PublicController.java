package org.hackncrypt.userservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.model.dto.LeaderboardDto;
import org.hackncrypt.userservice.model.dto.UserDto;
import org.hackncrypt.userservice.model.dto.response.UserDeviceTokenResponse;
import org.hackncrypt.userservice.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
public class PublicController {
    private final UserService userService;
    @GetMapping("/get-global-leaderboard")
    public ResponseEntity<List<LeaderboardDto>> getGlobalLeaderboard(){
        return ResponseEntity.ok(userService.fetchGlobalLeaderboardUserInfos());
    }
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsersLikeUsername(@RequestParam("username") String username){
        return ResponseEntity.ok(userService.searchUsersContainingUsername(username));
    }
    @GetMapping("/get-device-token/{userId}")
    public ResponseEntity<UserDeviceTokenResponse> getUserDeviceToken(@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUserDeviceToken(userId));
    }
}
