package org.hackncrypt.clanservice.integrations.fiegn.proxy.user;


import org.hackncrypt.clanservice.model.dto.response.AddUserClanResponse;
import org.hackncrypt.clanservice.model.dto.response.ApiSuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@FeignClient(name = "users",path = "/user")
public interface UserFeignProxy {
    @PutMapping("/api/v1/user/add-clan/{userId}/{clanId}")
    ResponseEntity<AddUserClanResponse> addUserClan(@PathVariable("userId") Long userId, @PathVariable("clanId") Long ClanId,
                                                    @RequestHeader("Authorization") String authHeader);

    @PutMapping("/api/v1/user/remove-clan/{userId}/{clanId}")
    ResponseEntity<AddUserClanResponse> removeUserClan(@PathVariable("userId") long userId,@PathVariable("clanId") Long clanId,@RequestHeader("Authorization") String authHeader);

    @PutMapping("/api/v1/user/clan-disband/{clanId}")
    ResponseEntity<ApiSuccessResponse> removeClanFromUsers(@PathVariable Long clanId,@RequestHeader("Authorization") String authHeader);

}
