package org.hackncrypt.submissionservice.services;

import org.hackncrypt.submissionservice.models.dto.SubmissionDto;
import org.hackncrypt.submissionservice.models.dto.request.SubmitSolutionRequest;
import org.hackncrypt.submissionservice.models.dto.response.SubmitSolutionResponse;
import org.hackncrypt.submissionservice.models.dto.response.UserSolvedSubmissionResponse;

import java.util.List;

public interface SubmissionService {
    SubmitSolutionResponse submitSolution(SubmitSolutionRequest submitSolutionRequest,Long userId,String authHeader);

    List<SubmissionDto> getProblemSubmission(String problemId, Long userId);

    List<UserSolvedSubmissionResponse> getUserSolvedSubmissions(Long userId, String authHeader);
}
