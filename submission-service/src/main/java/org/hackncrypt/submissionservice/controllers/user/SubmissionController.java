package org.hackncrypt.submissionservice.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.submissionservice.models.dto.SubmissionDto;
import org.hackncrypt.submissionservice.models.dto.response.SubmitSolutionResponse;
import org.hackncrypt.submissionservice.models.dto.request.SubmitSolutionRequest;
import org.hackncrypt.submissionservice.services.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;
    @PostMapping("/submit-solution")
    public ResponseEntity<SubmitSolutionResponse> submitSolution(@RequestBody @Valid SubmitSolutionRequest submitSolutionRequest,
                                                                 HttpServletRequest request){

        Long userId = Long.parseLong((String)request.getAttribute("userId"));
        log.info(String.valueOf(userId));
        return ResponseEntity.ok(submissionService.submitSolution(submitSolutionRequest,userId));
    }
    @GetMapping("/get-problem-submission/{problemId}")
    public ResponseEntity<List<SubmissionDto>> getAllProblemSubmission(@PathVariable String problemId, HttpServletRequest request){
        Long userId = Long.parseLong((String)request.getAttribute("userId"));
        log.info(String.valueOf(userId));
        return ResponseEntity.ok(submissionService.getProblemSubmission(problemId,userId));
    }

}
