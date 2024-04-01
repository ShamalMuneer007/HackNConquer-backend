package org.hackncrypt.testservice.services;


import org.hackncrypt.testservice.models.dto.TestCaseDto;
import org.hackncrypt.testservice.models.dto.request.AddTestCaseRequest;
import org.hackncrypt.testservice.models.dto.request.SubmitSolutionRequest;
import org.hackncrypt.testservice.models.dto.response.RunAndTestResponse;

import java.util.List;

public interface TestService {
    RunAndTestResponse runAndTest(SubmitSolutionRequest submitSolutionRequest);

    void addTestCase(AddTestCaseRequest addTestCaseRequest);

    List<TestCaseDto> getProblemTestCases(String problemId);
}
