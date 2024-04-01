package org.hackncrypt.testservice.controllers.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.submissionservice.models.dto.request.SubmitSolutionRequest;
import org.hackncrypt.testservice.models.dto.response.RunAndTestResponse;
import org.hackncrypt.testservice.services.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserTestController {
    private final TestService testService;
    @PostMapping("/run-and-test-solution")
    public ResponseEntity<RunAndTestResponse> runAndTestSolution(@RequestBody SubmitSolutionRequest submitSolutionRequest){
        return ResponseEntity.ok(testService.runAndTest(submitSolutionRequest));
    }
}
