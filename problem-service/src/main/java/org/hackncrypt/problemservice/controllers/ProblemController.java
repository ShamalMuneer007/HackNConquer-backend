package org.hackncrypt.problemservice.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.problemservice.model.dto.ProblemDto;
import org.hackncrypt.problemservice.model.dto.response.GetAllProblemResponse;
import org.hackncrypt.problemservice.services.problem.ProblemService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProblemController {
    private final ProblemService problemService;
    @GetMapping("/get-all-problems")
    public ResponseEntity<GetAllProblemResponse> getProblems(@RequestParam(defaultValue = "1") String page,
                                                             @RequestParam(defaultValue = "10") String size){
        log.info("Page No : {} ",page);
        Page<ProblemDto> problems = problemService.getAllProblem(Integer.parseInt(page), Integer.parseInt(size));

        return ResponseEntity.ok(new GetAllProblemResponse(problems, HttpStatus.OK.value()));
    }
    @GetMapping("/get-problem/{problemId}")
    public ResponseEntity<ProblemDto> getProblem(@PathVariable String problemId){
        log.info("Fetching problem info of id : {}",problemId);
        return ResponseEntity.ok(problemService.getProblemById(problemId));
    }
}
