package org.hackncrypt.problemservice.services.problem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.problemservice.enums.Difficulty;
import org.hackncrypt.problemservice.enums.SubmissionStatus;
import org.hackncrypt.problemservice.exceptions.*;
import org.hackncrypt.problemservice.exceptions.business.DuplicateValueException;
import org.hackncrypt.problemservice.exceptions.business.NoSuchValueException;
import org.hackncrypt.problemservice.exceptions.judge0.ClientSandboxCodeExecutionError;
import org.hackncrypt.problemservice.exceptions.judge0.SandboxCompileError;
import org.hackncrypt.problemservice.exceptions.judge0.SandboxError;
import org.hackncrypt.problemservice.exceptions.judge0.SandboxStandardError;
import org.hackncrypt.problemservice.model.dto.request.PatchProblemRequest;
import org.hackncrypt.problemservice.model.dto.request.AddProblemRequest;
import org.hackncrypt.problemservice.model.dto.request.Judge0Request;
import org.hackncrypt.problemservice.model.dto.request.ProblemVerificationRequest;
import org.hackncrypt.problemservice.model.dto.response.JudgeSubmissionResponse;
import org.hackncrypt.problemservice.model.dto.response.JudgeTokenResponse;
import org.hackncrypt.problemservice.model.dto.response.ProblemVerificationResponse;
import org.hackncrypt.problemservice.model.dto.testCases.AcceptedCase;
import org.hackncrypt.problemservice.model.dto.testCases.RejectedCase;
import org.hackncrypt.problemservice.model.dto.testCases.TestCase;
import org.hackncrypt.problemservice.model.entities.Category;
import org.hackncrypt.problemservice.model.entities.Problem;
import org.hackncrypt.problemservice.repositories.ProblemRepository;
import org.hackncrypt.problemservice.services.category.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final WebClient judgeWebClient;
    private final CategoryService categoryService;

    @Override
    public ProblemVerificationResponse verifyProblem(ProblemVerificationRequest problemVerificationDto) {
        Queue<AcceptedCase> acceptedCases = new ConcurrentLinkedQueue<>();
        Queue<RejectedCase> rejectedCases = new ConcurrentLinkedQueue<>();
        Queue<RuntimeException> exception = new ConcurrentLinkedQueue<>();
        Executor executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = problemVerificationDto.getTestCases()
                .stream()
                .map(test -> CompletableFuture.runAsync(() -> {
                    try {
                        JudgeSubmissionResponse submissionResponse = executeAndGetResponse(problemVerificationDto, test);
                        handleSubmissionResponse(submissionResponse, test, acceptedCases, rejectedCases);
                    }
                    catch (RuntimeException e) {
                        log.error(e.getMessage());
                        exception.add(e);
                    }
                },executor))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get(2, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("ERROR OCCURRED DURING SUBMISSION : {}", e.getMessage());
            throw new SandboxError(e,e.getMessage());
        }

        if (!exception.isEmpty()) {
            throw exception.peek();
        }

        if (!rejectedCases.isEmpty()) {
            log.info("All test cases does not match with given expected output");
            return new ProblemVerificationResponse("Expected output does not match with test cases given",
                    SubmissionStatus.REJECTED.name(), List.copyOf(acceptedCases), List.copyOf(rejectedCases));
        } else {
            log.info("Test cases Accepted!!!");
            return new ProblemVerificationResponse("Accepted",
                    SubmissionStatus.ACCEPTED.name(), List.copyOf(acceptedCases), List.copyOf(rejectedCases));
        }
    }

    @Override
    public void addProblem(AddProblemRequest addProblemRequest) {
        if(problemRepository.existsByProblemNameIgnoreCase(addProblemRequest.getProblemName())){
            log.warn("Problem with same name already exists");
            throw new DuplicateValueException("Problem with the same name already exists !!!");
        }
        log.info("Adding new problem ...");
        List<Category> categories = categoryService.getAllCategoriesOfCategoryNames(addProblemRequest.getCategories());
            Problem problem = Problem.builder()
                    .problemName(addProblemRequest.getProblemName())
                    .categories(categories)
                    .driverCode(addProblemRequest.getDriverCode())
                    .solutionTemplate(addProblemRequest.getSolutionTemplate())
                    .languageId(addProblemRequest.getLanguageId())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .testCases(addProblemRequest.getTestCases())
                    .isDeleted(false)
                    .difficulty(Difficulty.valueOf(addProblemRequest.getDifficulty().toUpperCase()))
                    .description(addProblemRequest.getDescription())
                    .build();
            problemRepository.save(problem);
    }

    @Override
    public void updateProblemDetails(long problemNo,PatchProblemRequest patchProblemRequest) {
        Problem problem = problemRepository.findByProblemNo(problemNo);
        if(Objects.equals(patchProblemRequest.getProblemName(), problem.getProblemName())){
            problem.setProblemName(patchProblemRequest.getProblemName());
        }
        if(Objects.equals(patchProblemRequest.getDescription(), problem.getDescription())){
            problem.setDescription(patchProblemRequest.getDescription());
        }
        if(Objects.equals(patchProblemRequest.getLanguageId(), problem.getLanguageId())){
            problem.setLanguageId(patchProblemRequest.getLanguageId());
        }
        if(patchProblemRequest.getSolutionTemplate() != null){
            problem.setSolutionTemplate(patchProblemRequest.getSolutionTemplate());
        }
        if(patchProblemRequest.getDriverCode() != null){
            problem.setDriverCode(patchProblemRequest.getDriverCode());
        }
        if(patchProblemRequest.getDifficulty()!= null &&
                !problem.getDifficulty().name().equals(patchProblemRequest.getDifficulty())){
            String[] allowedDifficulties = {Difficulty.HARD.name(),Difficulty.EASY.name(),Difficulty.MEDIUM.name()};
            String difficulty = patchProblemRequest.getDifficulty().toUpperCase();
            boolean isAllowed = Arrays.stream(allowedDifficulties).anyMatch(allowedDifficulty -> !allowedDifficulty.equals(difficulty));
            if(!isAllowed){
                log.warn("Invalid Difficulty! Please add a valid difficulty (EASY, MEDIUM, HARD).");
                throw new NoSuchValueException("Invalid Difficulty! Please add a valid difficulty (EASY, MEDIUM, HARD).");
            }
            problem.setDifficulty(Difficulty.valueOf(patchProblemRequest.getDifficulty().toUpperCase()));
        }
        if(patchProblemRequest.getTestCases() != null){
            problem.setTestCases(patchProblemRequest.getTestCases());
        }
        if(patchProblemRequest.getCategories() != null){
            List<Category> categories = categoryService.getAllCategoriesOfCategoryNames(patchProblemRequest.getCategories());
            problem.setCategories(categories);
        }
        problem.setUpdatedAt(LocalDateTime.now());
        problemRepository.save(problem);
    }


    // Initiates a Judge0 submission through a POST REST call at /submission, acquiring a token upon completion.
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
            throw new ClientSandboxCodeExecutionError(e,e.getMessage());
        } catch (Exception e) {
            log.error("HTTP REQUEST EXCEPTION : {}", e.getMessage());
            throw new SandboxError(e,e.getMessage());
        }
    }

    // Utilizing the submission token, an API call is executed to retrieve the details of the code execution performed in the submission.
    private JudgeSubmissionResponse executeAndGetResponse(ProblemVerificationRequest problemVerificationRequest, TestCase test){
        Judge0Request request = new Judge0Request();
        request.setSource_code(problemVerificationRequest.getSourceCode());
        request.setLanguage_id(problemVerificationRequest.getLanguageId());
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
                log.error("CLIENT ERROR : {}",e.getMessage());
                throw new ClientSandboxCodeExecutionError(e,e.getMessage());
            }
            catch (Exception e){
                log.error("ERROR : {}",e.getMessage());
                throw new SandboxError(e, e.getMessage());
            }
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                log.error("Thread interrupted while sleeping: {}", e.getMessage());
                throw new InternalServerException("Something went wrong while executing the program",e);
            }
        } while (Objects.requireNonNull(submissionResponse).getStatus().getId() < 3);
        return submissionResponse;
    }

    //Handles the compilation / standard error.
    //Checks whether the output of the program matches with the expected output given.
    //The output is accepted if it matches with the expected output. If it does not the output is then rejected!
    private void handleSubmissionResponse(JudgeSubmissionResponse submissionResponse, TestCase test, Queue<AcceptedCase> acceptedCases, Queue<RejectedCase> rejectedCases) {
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
        }
    }



}
