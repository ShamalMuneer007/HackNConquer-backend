package org.hackncrypt.submissionservice.integrations.testservice;

import org.hackncrypt.submissionservice.models.dto.request.SubmitSolutionRequest;
import org.hackncrypt.submissionservice.models.dto.response.RunAndTestResponse;
import org.springframework.http.ResponseEntity;

public class TestServiceFallback implements  TestFeignProxy{
    @Override
    public ResponseEntity<RunAndTestResponse> runAndTestSolution(SubmitSolutionRequest submitSolutionRequest, String authorizationHeader) {
        return null;
    }
}
