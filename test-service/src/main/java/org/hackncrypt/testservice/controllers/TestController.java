package org.hackncrypt.testservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.testservice.models.dto.TestCaseDto;
import org.hackncrypt.testservice.services.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TestController {
    private final TestService testService;
    @GetMapping("/get-problem-testcases/{problemId}")
    public ResponseEntity<List<TestCaseDto>> getProblemTestCases(@PathVariable String problemId){
        return ResponseEntity.ok(testService.getProblemTestCases(problemId));
    }
}
