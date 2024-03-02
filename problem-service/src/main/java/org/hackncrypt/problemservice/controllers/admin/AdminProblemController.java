package org.hackncrypt.problemservice.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.problemservice.annotations.Authorized;
import org.hackncrypt.problemservice.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
@Authorized("ROLE_ADMIN")
public class AdminProblemController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${judge0.base_url}")
    private String JUDGE_BASE_URI;
    @PostMapping("/verify-problem")
    public ResponseEntity<String> verifyProblem(@RequestBody ProblemVerificationDto problemVerificationDto,
                                                BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            FieldError error = bindingResult.getFieldError();
            return ResponseEntity.badRequest().body(error.getDefaultMessage());
        }
        try {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            CountDownLatch latch = new CountDownLatch(problemVerificationDto.getTestCases().size());
            log.info("No of test cases : {}",problemVerificationDto.getTestCases().size());
            for (TestCase test : problemVerificationDto.getTestCases()) {
                log.info("Test case  : {}",test.getTestCaseInput());
                log.info("Expected Output : {}",test.getExpectedOutput());
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(JUDGE_BASE_URI + "/submissions")
                        .queryParam("base64_encoded", "true");
                String uriWithParams = builder.toUriString();
                executor.submit(() -> {
                    log.info("Executing thread !!!!");
                    // Create judge request
                    Judge0Request request = new Judge0Request();
                    request.setSource_code(problemVerificationDto.getSourceCode());
                    request.setLanguage_id(problemVerificationDto.getLanguageId());
                    request.setStdin(Base64.getEncoder()
                            .encodeToString(test.getTestCaseInput().getBytes()));
                    log.info("Language ID : {}",request.getLanguage_id());
                    log.info("request object : {}",request  );
                    // Make judge request
                    // Convert the object to JSON
                    String jsonBody = convertObjectToJson(request);
                    // Set up headers

                    HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
                    JudgeSubmissionResponse submissionResponse;
                    try {
                        JudgeTokenResponse response = restTemplate.postForObject(uriWithParams, requestEntity, JudgeTokenResponse.class);
                        log.info("Response token ====> {}", response.getToken());
                        do {
                            try {
                                submissionResponse = restTemplate.getForObject(JUDGE_BASE_URI + "/submissions/" + response.getToken(), JudgeSubmissionResponse.class);
                            }
                            catch (Exception e){
                                log.error("Something went wrong while sending the request to judge0 {}",e.getMessage());
                                latch.countDown();
                                return;
                            }
                            try {
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException e) {
                                log.error("Thread interrupted while sleeping: {}", e.getMessage());
                            }
                        } while (submissionResponse.getStatus().getId() < 3);
                    }
                    catch (Exception e){
                        log.error("Something went wrong while sending the request to judge0 {}",e.getMessage());
                        latch.countDown();
                        return;
                    }
                    log.info("Response stdOut ==== > {}", submissionResponse.getStdout());
                    log.info("Response stdErr ==== > {}", submissionResponse.getStderr());
                    log.info("Response compile output ==== > {}", submissionResponse.getCompile_output());
                    latch.countDown();
                });
            }
            latch.await();
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body("Something went wrong in server side");
        }
        return ResponseEntity.ok("submission success!");
    }
    private String convertObjectToJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            // Handle the exception or log an error
            log.error("Something went wrong while converting object to json");
            return null;
        }
    }
}
