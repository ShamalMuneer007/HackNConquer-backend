package org.hackncrypt.problemservice.controllers.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.problemservice.annotations.Authorized;
import org.hackncrypt.problemservice.enums.SubmissionStatus;
import org.hackncrypt.problemservice.exceptions.judge0.ClientSandboxCodeExecutionError;
import org.hackncrypt.problemservice.exceptions.judge0.SandboxCompileError;
import org.hackncrypt.problemservice.exceptions.judge0.SandboxError;
import org.hackncrypt.problemservice.exceptions.judge0.SandboxStandardError;
import org.hackncrypt.problemservice.model.dto.response.ApiSuccessResponse;
import org.hackncrypt.problemservice.model.dto.request.PatchProblemRequest;
import org.hackncrypt.problemservice.model.dto.request.AddProblemRequest;
import org.hackncrypt.problemservice.model.dto.request.ProblemVerificationRequest;
import org.hackncrypt.problemservice.model.dto.response.ProblemVerificationResponse;
import org.hackncrypt.problemservice.services.problem.ProblemService;
import org.hackncrypt.problemservice.exceptions.judge0.TestCaseTimeOutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ProblemVerificationResponse> verifyProblem(@Valid @RequestBody ProblemVerificationRequest problemVerificationRequest){
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
            log.error("Client ERROR : {}",e.getMessage());
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

    @PostMapping("/add-problem")
    public ResponseEntity<ApiSuccessResponse> addProblem(@Valid @RequestBody AddProblemRequest addProblemRequest, HttpServletRequest servletRequest){
        log.info("Adding problem");
        problemService.addProblem(addProblemRequest,servletRequest);
        return ResponseEntity.ok(new ApiSuccessResponse("Problem added successfully", HttpStatus.OK.value()));
    }
    @DeleteMapping("/delete-problem/{problemId}")
    public ResponseEntity<ApiSuccessResponse> deleteProblem(@PathVariable String problemId){
        log.info("Inside delete problem request controller method....");
        problemService.deleteProblem(problemId);
        return ResponseEntity.ok(new ApiSuccessResponse("Problem removed successfully",HttpStatus.OK.value()));
    }
    @PatchMapping("/edit-problem/{problemId}")
    public ResponseEntity<ApiSuccessResponse> patchProblem(@Valid @RequestBody PatchProblemRequest patchProblemRequest, @PathVariable String problemId){
        log.info("Inside edit problem request controller method....");
        problemService.updateProblemDetails(problemId,patchProblemRequest);
        return ResponseEntity.ok(new ApiSuccessResponse("Problem updated successfully", HttpStatus.OK.value()));
    }


}
