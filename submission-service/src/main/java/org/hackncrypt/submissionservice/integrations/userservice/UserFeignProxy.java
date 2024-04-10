package org.hackncrypt.submissionservice.integrations.userservice;

import org.hackncrypt.submissionservice.models.dto.request.IncreaseXpRequest;
import org.hackncrypt.submissionservice.models.dto.response.ApiSuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "users", path = "/user/api/v1")
public interface UserFeignProxy {
    @PostMapping("/user/increase-xp")
    ResponseEntity<ApiSuccessResponse> increaseUserLevel(@RequestBody IncreaseXpRequest increaseXpRequest, @RequestHeader("Authorization") String authorizationHeader);
}
