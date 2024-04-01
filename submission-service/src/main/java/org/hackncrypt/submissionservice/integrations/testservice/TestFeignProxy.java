package org.hackncrypt.submissionservice.integrations.testservice;

import org.hackncrypt.submissionservice.models.dto.request.SubmitSolutionRequest;
import org.hackncrypt.submissionservice.models.dto.response.RunAndTestResponse;
import org.hackncrypt.submissionservice.models.dto.response.SubmitSolutionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "test",path = "/test")
public interface TestFeignProxy {

    @PostMapping("/api/v1/user/run-and-test-solution")
    ResponseEntity<RunAndTestResponse> runAndTestSolution(@RequestBody SubmitSolutionRequest submitSolutionRequest, @RequestHeader("Authorization") String authorizationHeader);
}
