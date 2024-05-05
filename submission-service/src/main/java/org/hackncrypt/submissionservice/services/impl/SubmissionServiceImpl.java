package org.hackncrypt.submissionservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.submissionservice.enums.SubmissionStatus;
import org.hackncrypt.submissionservice.exceptions.technical.SolutionSubmissionException;
import org.hackncrypt.submissionservice.integrations.problemservice.ProblemFeignProxy;
import org.hackncrypt.submissionservice.integrations.testservice.TestFeignProxy;
import org.hackncrypt.submissionservice.integrations.userservice.UserFeignProxy;
import org.hackncrypt.submissionservice.models.dto.SolvedProblem;
import org.hackncrypt.submissionservice.models.dto.SubmissionDto;
import org.hackncrypt.submissionservice.models.dto.request.ChangeProblemAcceptanceRateRequest;
import org.hackncrypt.submissionservice.models.dto.request.IncreaseXpRequest;
import org.hackncrypt.submissionservice.models.dto.request.SubmitSolutionRequest;
import org.hackncrypt.submissionservice.models.dto.response.RunAndTestResponse;
import org.hackncrypt.submissionservice.models.dto.response.SubmitSolutionResponse;
import org.hackncrypt.submissionservice.models.dto.response.UserSolvedSubmissionResponse;
import org.hackncrypt.submissionservice.models.entities.Submission;
import org.hackncrypt.submissionservice.repositories.SubmissionRepository;
import org.hackncrypt.submissionservice.services.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final TestFeignProxy testFeignProxy;
    private final UserFeignProxy userFeignProxy;
    private final ProblemFeignProxy problemFeignProxy;
    @Override
    public SubmitSolutionResponse submitSolution(SubmitSolutionRequest submitSolutionRequest, Long userId, String authHeader) {
        ResponseEntity<RunAndTestResponse> response =  testFeignProxy.runAndTestSolution(submitSolutionRequest,authHeader);
        RunAndTestResponse solutionResponse = response.getBody();
        if(response.getStatusCode() != HttpStatus.OK || solutionResponse == null){
            throw new SolutionSubmissionException("No solution Response !!!");
        }
        List<Submission> solvedSubmissions = submissionRepository.findByUserIdAndSubmissionStatus(userId,SubmissionStatus.ACCEPTED);
        List<Submission> problemSubmissions = solvedSubmissions.stream()
                .filter(submission -> submission.getProblemId().equals(submitSolutionRequest.getProblemId())).toList();

        if(solutionResponse.getSubmissionStatus().equals(SubmissionStatus.ACCEPTED)){
            if(problemSubmissions.isEmpty()) {
                IncreaseXpRequest increaseXpRequest =
                        new IncreaseXpRequest(userId, submitSolutionRequest.getProblemLevel() * 10);
                userFeignProxy.increaseUserLevel(increaseXpRequest, authHeader);
            }
            else{
                problemSubmissions.stream()
                        .min(Comparator.comparingDouble(Submission::getAverageMemory)
                                .thenComparingDouble(Submission::getAverageTime))
                        .filter(s -> s.getAverageMemory() != null && s.getAverageTime() != null)
                        .ifPresent(solvedSubmission -> {
                            if(solutionResponse.getAverageMemory() < solvedSubmission.getAverageMemory()  ||  solutionResponse.getAverageTime() < solvedSubmission.getAverageTime() ){
                                IncreaseXpRequest increaseXpRequest =
                                        new IncreaseXpRequest(userId, submitSolutionRequest.getProblemLevel() * 4);
                                userFeignProxy.increaseUserLevel(increaseXpRequest, authHeader);
                            }
                        });
            }
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
       List<Submission> submissions = submissionRepository.findByProblemId(submitSolutionRequest.getProblemId());
        AtomicInteger acceptedSubmission = new AtomicInteger();
        submissions.forEach(sub -> {
            if (sub.getSubmissionStatus().equals(SubmissionStatus.ACCEPTED)) {
                acceptedSubmission.getAndIncrement();
            }
        });
        float acceptanceRate = (float) acceptedSubmission.get() / submissions.size() * 100;
        acceptanceRate = Math.round(acceptanceRate * 100) / 100f;
        problemFeignProxy.changeProblemAcceptanceRate(new ChangeProblemAcceptanceRateRequest(submitSolutionRequest.getProblemId(),acceptanceRate),authHeader);
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

    @Override
    public List<UserSolvedSubmissionResponse> getUserSolvedSubmissions(Long userId,String authHeader) {
        List<Submission> solvedSubmissions = submissionRepository.findByUserIdAndSubmissionStatus(userId,SubmissionStatus.ACCEPTED);
        List<String> solvedProblemIdList = solvedSubmissions.stream()
                .map(Submission::getProblemId)
                .toList();
        List<UserSolvedSubmissionResponse> solvedProblems = problemFeignProxy.getProblemsByProblemIdList(solvedProblemIdList,authHeader);
        return solvedProblems.stream()
                .peek(solvedProblem -> {
                    Optional<Submission> submission = solvedSubmissions.stream()
                            .filter(solvedSubmission -> solvedSubmission.getProblemId().equals(solvedProblem.getProblemId()))
                            .min(Comparator.comparingDouble(Submission::getAverageMemory)
                                    .thenComparingDouble(Submission::getAverageTime))
                            .filter(s -> s.getAverageMemory() != null && s.getAverageTime() != null);
                    submission.ifPresent(solvedSubmission -> {
                        solvedProblem.setSolvedAt(solvedSubmission.getSubmittedAt().toLocalDate());
                        solvedProblem.setSolutionCode(solvedSubmission.getSolutionCode());
                        solvedProblem.setBestMemory(solvedSubmission.getAverageMemory());
                        solvedProblem.setBestRuntime(solvedSubmission.getAverageTime());
                    });
                }).sorted(Comparator.comparing(UserSolvedSubmissionResponse::getSolvedAt, Comparator.reverseOrder()))
                .toList();
    }
}
