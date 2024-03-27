package org.hackncrypt.submissionservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.submissionservice.exceptions.technical.SolutionSubmissionException;
import org.hackncrypt.submissionservice.integrations.testservice.TestFeignProxy;
import org.hackncrypt.submissionservice.models.dto.SubmissionDto;
import org.hackncrypt.submissionservice.models.dto.request.SubmitSolutionRequest;
import org.hackncrypt.submissionservice.models.dto.response.RunAndTestResponse;
import org.hackncrypt.submissionservice.models.dto.response.SubmitSolutionResponse;
import org.hackncrypt.submissionservice.models.entities.Submission;
import org.hackncrypt.submissionservice.repositories.SubmissionRepository;
import org.hackncrypt.submissionservice.services.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final TestFeignProxy testFeignProxy;
    @Override
    public SubmitSolutionResponse submitSolution(SubmitSolutionRequest submitSolutionRequest, Long userId) {
        ResponseEntity<RunAndTestResponse> response =  testFeignProxy.runAndTestSolution(submitSolutionRequest);
        RunAndTestResponse solutionResponse = response.getBody();
        if(response.getStatusCode() == HttpStatus.OK &&  solutionResponse != null){
            Submission submission = Submission
                    .builder()
                    .submissionStatus(solutionResponse.getSubmissionStatus())
                    .averageMemory(solutionResponse.getAverageMemory())
                    .averageMemory(solutionResponse.getAverageMemory())
                    .problemId(submitSolutionRequest.getProblemId())
                    .userId(userId)
                    .build();
            submissionRepository.save(submission);
            return new SubmitSolutionResponse(solutionResponse);
        }
        else{
            throw new SolutionSubmissionException("");
        }
    }

    @Override
    public List<SubmissionDto> getProblemSubmission(String problemId, Long userId) {
        List<Submission> submissions = submissionRepository.findByProblemIdAndUserId(problemId,userId);
        List<SubmissionDto> submissionDtos = new ArrayList<>();
        submissions.forEach(submission -> {
            SubmissionDto submissionDto = new SubmissionDto(submission);
            submissionDtos.add(submissionDto);
        });
        return submissionDtos;
    }
}
