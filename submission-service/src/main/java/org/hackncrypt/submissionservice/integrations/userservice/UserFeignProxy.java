package org.hackncrypt.submissionservice.integrations.userservice;

import org.hackncrypt.submissionservice.models.dto.request.IncreaseXpRequest;
import org.hackncrypt.submissionservice.models.dto.response.ApiSuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user", path = "/user")
public interface UserFeignProxy {
    @PostMapping("/api/v1/user/increase-xp")
    ResponseEntity<ApiSuccessResponse> increaseUserLevel(@RequestBody IncreaseXpRequest increaseXpRequest);
}
