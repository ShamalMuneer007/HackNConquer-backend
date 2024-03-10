package org.hackncrypt.problemservice.controllers.admin;

import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.problemservice.annotations.Authorized;
import org.hackncrypt.problemservice.enums.SubmissionStatus;
import org.hackncrypt.problemservice.exceptions.ClientSandboxCodeExecutionError;
import org.hackncrypt.problemservice.exceptions.SandboxCompileError;
import org.hackncrypt.problemservice.exceptions.SandboxError;
import org.hackncrypt.problemservice.exceptions.SandboxStandardError;
import org.hackncrypt.problemservice.model.dto.Request.ProblemVerificationRequest;
import org.hackncrypt.problemservice.model.dto.Response.ProblemVerificationResponse;
import org.hackncrypt.problemservice.services.ProblemService;
import org.hackncrypt.problemservice.exceptions.TestCaseTimeOutException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
@Authorized("ROLE_ADMIN")
public class AdminProblemController {

    private final ProblemService problemService;

    public AdminProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping("/verify-problem")
    public ResponseEntity<ProblemVerificationResponse> verifyProblem(@RequestBody ProblemVerificationRequest problemVerificationRequest,
                                                                     BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            FieldError error = bindingResult.getFieldError();
            return ResponseEntity.badRequest().body(ProblemVerificationResponse.builder()
                            .message(error.getDefaultMessage())
                    .build());
        }
        if(problemVerificationRequest.getTestCases().get(0).getTestCaseInput().isEmpty() ||
                problemVerificationRequest.getTestCases().get(0).getExpectedOutput().isEmpty()){
            return ResponseEntity.badRequest().body(ProblemVerificationResponse
                    .builder()
                            .message("Invalid test cases !!!")
                    .build());
        }
        ProblemVerificationResponse problemVerificationResponse;
        try{
          problemVerificationResponse =  problemService.verifyProblem(problemVerificationRequest);
        }
        catch (TestCaseTimeOutException e){
            log.warn("Timed out!!!");
            return ResponseEntity.badRequest().build();
        }
        catch (SandboxCompileError e){
            log.error("Compile ERROR {}",e.getMessage());
            return ResponseEntity.badRequest().body(ProblemVerificationResponse.builder()
                    .message(e.getMessage())
                    .status(SubmissionStatus.COMPILE_ERR.name())
                    .build());
        }
        catch (SandboxStandardError e){
            log.warn("Standard Error {}",e.getMessage());
            return ResponseEntity.badRequest().body(ProblemVerificationResponse.builder()
                    .message(e.getMessage())
                    .status(SubmissionStatus.STD_ERR.name())
                    .build());
        }
        catch (ClientSandboxCodeExecutionError e){
            log.error("Cient ERROR : {}",e.getMessage());
            return ResponseEntity.badRequest().body(ProblemVerificationResponse.builder()
                    .message("client error"+e.getMessage())
                    .build());
        }
        catch (SandboxError e){
            return ResponseEntity.internalServerError().body(ProblemVerificationResponse.builder()
                    .message(e.getMessage())
                    .build());
        }
        catch (Exception e){
            log.error("Something went wrong while processing the request !!! : {}",e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(problemVerificationResponse);
    }


}
