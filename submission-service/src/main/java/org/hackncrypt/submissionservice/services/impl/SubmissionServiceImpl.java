package org.hackncrypt.submissionservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.submissionservice.enums.SubmissionStatus;
import org.hackncrypt.submissionservice.exceptions.technical.SolutionSubmissionException;
import org.hackncrypt.submissionservice.integrations.testservice.TestFeignProxy;
import org.hackncrypt.submissionservice.integrations.userservice.UserFeignProxy;
import org.hackncrypt.submissionservice.models.dto.SubmissionDto;
import org.hackncrypt.submissionservice.models.dto.request.IncreaseXpRequest;
import org.hackncrypt.submissionservice.models.dto.request.SubmitSolutionRequest;
import org.hackncrypt.submissionservice.models.dto.response.RunAndTestResponse;
import org.hackncrypt.submissionservice.models.dto.response.SubmitSolutionResponse;
import org.hackncrypt.submissionservice.models.entities.Submission;
import org.hackncrypt.submissionservice.repositories.SubmissionRepository;
import org.hackncrypt.submissionservice.services.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final TestFeignProxy testFeignProxy;
    private final UserFeignProxy userFeignProxy;
    @Override
    public SubmitSolutionResponse submitSolution(SubmitSolutionRequest submitSolutionRequest, Long userId, String authHeader) {
        ResponseEntity<RunAndTestResponse> response =  testFeignProxy.runAndTestSolution(submitSolutionRequest,authHeader);
        RunAndTestResponse solutionResponse = response.getBody();
        if(response.getStatusCode() != HttpStatus.OK || solutionResponse == null){
            throw new SolutionSubmissionException("No solution Response !!!");
        }
        if(solutionResponse.getSubmissionStatus().equals(SubmissionStatus.ACCEPTED)){
            IncreaseXpRequest increaseXpRequest =
                    new IncreaseXpRequest(userId,submitSolutionRequest.getProblemLevel()*10);
            userFeignProxy.increaseUserLevel(increaseXpRequest);
        }
        Submission submission = Submission
                .builder()
                .submissionStatus(solutionResponse.getSubmissionStatus())
                .averageMemory(solutionResponse.getAverageMemory())
                .averageTime(solutionResponse.getAverageTime())
                .problemId(submitSolutionRequest.getProblemId())
                .userId(userId)
                .submittedAt(LocalDateTime.now())
                .solutionCode(submitSolutionRequest.getSolutionCode())
                .acceptedCases(solutionResponse.getAcceptedCases())
                .rejectedCases(solutionResponse.getRejectedCases())
                .totalTestCases(solutionResponse.getTotalTestCases())
                .build();
        submissionRepository.save(submission);
        return new SubmitSolutionResponse(solutionResponse);

    }

    @Override
    public List<SubmissionDto> getProblemSubmission(String problemId, Long userId) {
        List<Submission> submissions = submissionRepository.findByProblemIdAndUserId(problemId,userId);
        List<SubmissionDto> submissionDtos = new ArrayList<>();
        submissions.forEach(submission -> {
            log.info(submission.toString());
            SubmissionDto submissionDto = new SubmissionDto(submission);
            submissionDtos.add(submissionDto);
        });
        return submissionDtos;
    }
}
