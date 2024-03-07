package org.hackncrypt.problemservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.concurrent.CompletedFuture;
import org.hackncrypt.problemservice.enums.SubmissionStatus;
import org.hackncrypt.problemservice.exceptions.*;
import org.hackncrypt.problemservice.model.dto.*;
import org.hackncrypt.problemservice.repositories.ProblemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {
    private final ProblemRepository problemRepository;
    private final WebClient judgeWebClient;



    @Override
    public ProblemVerificationResponse verifyProblem(ProblemVerificationDto problemVerificationDto) {
        Queue<AcceptedCase> acceptedCases = new ConcurrentLinkedQueue<>();
        Queue<RejectedCase> rejectedCases = new ConcurrentLinkedQueue<>();
        Queue<RuntimeException> exception = new ConcurrentLinkedQueue<>();
        Executor executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = problemVerificationDto.getTestCases()
                .stream()
                .map(test -> CompletableFuture.runAsync(() -> {
                    try {
                        log.info("Executing thread !!!! {}", Thread.currentThread());
                        JudgeSubmissionResponse submissionResponse = executeAndGetResponse(problemVerificationDto, test);
                        handleSubmissionResponse(submissionResponse, test, acceptedCases, rejectedCases);
                    } catch (RuntimeException e) {
                        log.error(e.getMessage());
                        exception.add(e);
                    }
                },executor))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get(2, TimeUnit.MINUTES); // Adjust the timeout as needed
        } catch (Exception e) {
            log.error("ERROR OCCURRED DURING SUBMISSION : {}", e.getMessage());
            throw new RuntimeException(e);
        }

        if (!exception.isEmpty()) {
            throw exception.peek();
        }

        if (!rejectedCases.isEmpty()) {
            log.info("Accepted!!!");
            return new ProblemVerificationResponse("Expected output does not match with test cases given",
                    SubmissionStatus.REJECTED.name(), List.copyOf(acceptedCases), List.copyOf(rejectedCases));
        } else {
            log.info("Rejected!!!");
            return new ProblemVerificationResponse("Accepted",
                    SubmissionStatus.ACCEPTED.name(), List.copyOf(acceptedCases), List.copyOf(rejectedCases));
        }
    }

    private JudgeTokenResponse createJudge0Submission(Judge0Request judge0Request){
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
            throw new ClientSandboxCodeExecutionError(e.getMessage());
        } catch (Exception e) {
            log.error("HTTP REQUEST EXCEPTION ERROR : {}", e.getMessage());
            throw new SandboxError(e.getMessage());
        }
    }
    private JudgeSubmissionResponse executeAndGetResponse(ProblemVerificationDto problemVerificationDto, TestCase test){
        Judge0Request request = new Judge0Request();
        request.setSource_code(problemVerificationDto.getSourceCode());
        request.setLanguage_id(problemVerificationDto.getLanguageId());
        request.setStdin(Base64.getEncoder().encodeToString(test.getTestCaseInput().getBytes()));

        JudgeTokenResponse submissionCreationResponse = createJudge0Submission(request);
        JudgeSubmissionResponse submissionResponse = new JudgeSubmissionResponse();

        do {
            try {
                submissionResponse = judgeWebClient.get()
                        .uri("/submissions/" + submissionCreationResponse.getToken())
                        .retrieve()
                        .bodyToMono(JudgeSubmissionResponse.class)
                        .block();
            }
            catch (HttpClientErrorException e){
                log.error("ERROR : {}",e.getMessage());
                throw new ClientSandboxCodeExecutionError(e.getMessage());
            }
            catch (Exception e){
                log.error("ERROR : {}",e.getMessage());
                throw new SandboxError(e.getMessage());
            }
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                log.error("Thread interrupted while sleeping: {}", e.getMessage());
                throw new InternalServerException("Something went wrong while executing the program");
            }
        } while (Objects.requireNonNull(submissionResponse).getStatus().getId() < 3);

        return submissionResponse;
    }

    private void handleSubmissionResponse(JudgeSubmissionResponse submissionResponse, TestCase test, Queue<AcceptedCase> acceptedCases, Queue<RejectedCase> rejectedCases) {
        if (submissionResponse.getCompile_output() != null) {
            log.warn("ERROR IN SUBMITTED CODE : {}", submissionResponse.getCompile_output());
            throw new SandboxCompileError(submissionResponse.getCompile_output());
        } else if (submissionResponse.getStderr() != null) {
            log.warn("ERROR IN SUBMITTED CODE : {}", submissionResponse.getStderr());
            throw new SandboxStandardError(submissionResponse.getStderr());
        } else if (submissionResponse.getStdout() != null) {
            String output = submissionResponse.getStdout().replace("\n", "");
            if (test.getExpectedOutput().equals(output)) {
                log.info("ACCEPTED!!!");
                acceptedCases.add(new AcceptedCase(test.getTestCaseInput(), test.getExpectedOutput()));
            } else {
                log.info("Rejected!!!");
                rejectedCases.add(new RejectedCase(test.getTestCaseInput(), output, test.getExpectedOutput()));
            }
        }
    }

    private void shutdownExecutorService(ExecutorService executor) {
        try {
            executor.shutdown();
            boolean terminated = executor.awaitTermination(2, TimeUnit.MINUTES);
            if (!terminated) {
                executor.shutdownNow();
                throw new TestCaseTimeOutException("Verification of testcases timed out");
            }
        } catch (InterruptedException e) {
            log.error("Error shutting down executor {}", e.getMessage());
            throw new InternalServerException(e.getMessage());
        } finally {
            executor.shutdownNow();
        }
    }


}
