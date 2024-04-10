package org.hackncrypt.submissionservice.integrations.problemservice;

import org.hackncrypt.submissionservice.models.dto.request.ChangeProblemAcceptanceRateRequest;
import org.hackncrypt.submissionservice.models.dto.response.ApiSuccessResponse;
import org.hackncrypt.submissionservice.models.dto.response.UserSolvedSubmissionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name="problem" , path = "/problem/api/v1")
public interface ProblemFeignProxy {
    @PostMapping("/user/change-acceptance-rate")
    ResponseEntity<ApiSuccessResponse> changeProblemAcceptanceRate(@RequestBody ChangeProblemAcceptanceRateRequest changeProblemAcceptanceRateRequest, @RequestHeader("Authorization") String authorizationHeader);

    @GetMapping("/user/get-problems-by-problemid-list")
    List<UserSolvedSubmissionResponse> getProblemsByProblemIdList(@RequestParam("problemIdList") List<String> solvedProblemIdList, @RequestHeader("Authorization")String authHeader);
}
