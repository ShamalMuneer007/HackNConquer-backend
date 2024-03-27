package org.hackncrypt.testservice.services;

import org.hackncrypt.problemservice.model.dto.request.AddTestCaseRequest;
import org.hackncrypt.submissionservice.models.dto.request.SubmitSolutionRequest;
import org.hackncrypt.testservice.models.dto.response.RunAndTestResponse;

public interface TestService {
    RunAndTestResponse runAndTest(SubmitSolutionRequest submitSolutionRequest);

    void addTestCase(AddTestCaseRequest addTestCaseRequest);
}
