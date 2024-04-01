package org.hackncrypt.testservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.testservice.enums.SubmissionStatus;
import org.hackncrypt.testservice.exceptions.judge0.ClientSandboxCodeExecutionError;
import org.hackncrypt.testservice.exceptions.judge0.SandboxCompileError;
import org.hackncrypt.testservice.exceptions.judge0.SandboxError;
import org.hackncrypt.testservice.exceptions.judge0.SandboxStandardError;
import org.hackncrypt.testservice.exceptions.technical.InternalServerException;
import org.hackncrypt.testservice.models.dto.TestCaseDto;
import org.hackncrypt.testservice.models.dto.request.AddTestCaseRequest;
import org.hackncrypt.testservice.models.dto.request.Judge0Request;
import org.hackncrypt.testservice.models.dto.request.SubmitSolutionRequest;
import org.hackncrypt.testservice.models.dto.response.JudgeSubmissionResponse;
import org.hackncrypt.testservice.models.dto.response.JudgeTokenResponse;
import org.hackncrypt.testservice.models.dto.response.RunAndTestResponse;
import org.hackncrypt.testservice.models.dto.testCases.AcceptedCase;
import org.hackncrypt.testservice.models.dto.testCases.RejectedCase;
import org.hackncrypt.testservice.models.dto.testCases.TestCase;
import org.hackncrypt.testservice.models.entities.Test;
import org.hackncrypt.testservice.repositories.TestRepository;
import org.hackncrypt.testservice.services.TestService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;
    private final WebClient judgeWebClient;

    @Override
    public RunAndTestResponse runAndTest(SubmitSolutionRequest submitSolutionRequest) {
        Queue<AcceptedCase> acceptedCases = new ConcurrentLinkedQueue<>();
        Queue<RejectedCase> rejectedCases = new ConcurrentLinkedQueue<>();
        Queue<RuntimeException> exception = new ConcurrentLinkedQueue<>();
        Queue<Double> timeTaken = new ConcurrentLinkedQueue<>();
        Queue<Double> memoryTaken = new ConcurrentLinkedQueue<>();
        List<Test> allTestCases = testRepository.findByProblemId(submitSolutionRequest.getProblemId());
        Executor executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = allTestCases.get(0).getTestCases()
                .stream()
                .map(test -> CompletableFuture.runAsync(() -> {
                    try {
                        JudgeSubmissionResponse submissionResponse = executeAndGetResponse(submitSolutionRequest, test);
                        handleSubmissionResponse(submissionResponse, test, acceptedCases, rejectedCases, timeTaken, memoryTaken);
                    } catch (RuntimeException e) {
                        log.error(e.getMessage());
                        exception.add(e);
                    }
                }, executor))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get(2, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("ERROR OCCURRED DURING SUBMISSION : {}", e.getMessage());
            throw new SandboxError(e, e.getMessage());
        }

        if (!exception.isEmpty()) {
            throw exception.peek();
        }
        RunAndTestResponse response = new RunAndTestResponse();
        double avgMemory = memoryTaken.stream().reduce(0D, Double::sum) / memoryTaken.size();
        double avgTime = timeTaken.stream().reduce(0D, Double::sum) / timeTaken.size();
        double averageMemoryInMB = avgMemory / 1024.0;
        double averageTimeInSeconds = avgTime * 1000.0;
        response.setAverageMemory(Math.round(averageMemoryInMB*100.0)/100.0);
        response.setAverageTime(Math.round(averageTimeInSeconds*100.0)/100.0);
        if (!rejectedCases.isEmpty()) {
            log.info("All test cases does not match with given expected output");
            response.setSubmissionStatus(SubmissionStatus.REJECTED);
        } else {
            response.setSubmissionStatus(SubmissionStatus.ACCEPTED);
            log.info("Test cases Accepted!!!");
        }
        response.setRejectedCases(rejectedCases.stream().toList());
        response.setAcceptedCases(acceptedCases.stream().toList());
        response.setTotalTestCases(allTestCases.get(0).getTestCases().size());
        return response;
    }

    @Override
    public void addTestCase(AddTestCaseRequest addTestCaseRequest) {
        Test test = Test.builder()
                .problemId(addTestCaseRequest.getProblemId())
                .testCases(addTestCaseRequest.getTestCases())
                .build();
        log.info(addTestCaseRequest.getProblemId());
        log.info(String.valueOf(addTestCaseRequest.getTestCases()));
        log.info(String.valueOf(test));
        log.info("||| SAVING TEST CASE |||");
        testRepository.save(test);
    }

    @Override
    public List<TestCaseDto> getProblemTestCases(String problemId) {
        List<TestCase> tests = testRepository.findByProblemId(problemId).get(0).getTestCases();
        List<TestCaseDto> testCaseDtoList = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(0);
        tests.forEach(test -> {
            index.getAndIncrement();
            testCaseDtoList.add(new TestCaseDto(test,index.get()));
        });
        return testCaseDtoList;
    }


    // Initiates a Judge0 submission through a POST REST call at /submission, acquiring a token upon completion.
    private JudgeTokenResponse createJudge0Submission(Judge0Request judge0Request) {
        try {
            log.info("request object : {}", judge0Request);
            return judgeWebClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/submissions")
                            .queryParam("base64_encoded", "true")
                            .build())
                    .bodyValue(judge0Request)
                    .retrieve()
                    .bodyToMono(JudgeTokenResponse.class)
                    .block(); // This will block until the response is received or an error occurs
        } catch (HttpClientErrorException e) {
            log.error("HTTP CLIENT ERROR : {}", e.getMessage());
            throw new ClientSandboxCodeExecutionError(e, e.getMessage());
        } catch (Exception e) {
            log.error("HTTP REQUEST EXCEPTION : {}", e.getMessage());
            throw new SandboxError(e, e.getMessage());
        }
    }

    // Utilizing the submission token, an API call is executed to retrieve the details of the code execution performed in the submission.
    private JudgeSubmissionResponse executeAndGetResponse(SubmitSolutionRequest submitSolutionRequest, TestCase test) {
        Judge0Request request = new Judge0Request();
        String srcCode = submitSolutionRequest.getDriverCode() + submitSolutionRequest.getSolutionCode();
        String encodedSrcCode = Base64.getEncoder().encodeToString(srcCode.getBytes());
        request.setSource_code(encodedSrcCode);
        request.setLanguage_id(submitSolutionRequest.getLanguageId());
        request.setStdin(Base64.getEncoder().encodeToString(test.getTestCaseInput().getBytes()));

        JudgeTokenResponse submissionCreationResponse = createJudge0Submission(request);
        JudgeSubmissionResponse submissionResponse;

        do {
            try {
                submissionResponse = judgeWebClient.get()
                        .uri("/submissions/" + submissionCreationResponse.getToken())
                        .retrieve()
                        .bodyToMono(JudgeSubmissionResponse.class)
                        .block();
            } catch (HttpClientErrorException e) {
                log.error("CLIENT ERROR : {}", e.getMessage());
                throw new ClientSandboxCodeExecutionError(e, e.getMessage());
            } catch (Exception e) {
                log.error("ERROR : {}", e.getMessage());
                throw new SandboxError(e, e.getMessage());
            }
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                log.error("Thread interrupted while sleeping: {}", e.getMessage());
                throw new InternalServerException("Something went wrong while executing the program", e);
            }
        } while (Objects.requireNonNull(submissionResponse).getStatus().getId() < 3);
        return submissionResponse;
    }

    //Handles the compilation / standard error.
    //Checks whether the output of the program matches with the expected output given.
    //The output is accepted if it matches with the expected output. If it does not the output is then rejected!
    private void handleSubmissionResponse(JudgeSubmissionResponse submissionResponse,
                                          TestCase test,
                                          Queue<AcceptedCase> acceptedCases,
                                          Queue<RejectedCase> rejectedCases,
                                          Queue<Double> timeTaken, Queue<Double> memoryTaken) {
        if (submissionResponse.getCompile_output() != null) {
            log.warn("ERROR IN SUBMITTED CODE : COMPILE_OUTPUT {}", submissionResponse.getCompile_output());
            throw new SandboxCompileError(submissionResponse.getCompile_output());
        } else if (submissionResponse.getStderr() != null) {
            log.warn("ERROR IN SUBMITTED CODE : STDERR {}", submissionResponse.getStderr());
            throw new SandboxStandardError(submissionResponse.getStderr());
        } else if (submissionResponse.getStdout() != null) {
            String output = submissionResponse.getStdout().replace("\n", "");
            if (test.getExpectedOutput().equals(output)) {
                log.info("Test case Accepted!!!");
                acceptedCases.add(new AcceptedCase(test.getTestCaseInput(), test.getExpectedOutput()));
            } else {
                log.info("Test case Rejected!!!");
                rejectedCases.add(new RejectedCase(test.getTestCaseInput(), output, test.getExpectedOutput()));
            }
            timeTaken.add(Double.parseDouble(submissionResponse.getTime()));
            memoryTaken.add(Double.parseDouble(submissionResponse.getMemory()));
        }
    }
}
