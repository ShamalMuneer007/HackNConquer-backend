package org.hackncrypt.testservice.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.problemservice.model.dto.request.AddTestCaseRequest;
import org.hackncrypt.testservice.services.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminTestController {
    private final TestService testService;
    @PostMapping("/add-testcase")
    public ResponseEntity<String> addProblemTestCases(@RequestBody AddTestCaseRequest addTestCaseRequest){
        log.info("||| ADD TEST CASE |||");
        log.info("data {}", addTestCaseRequest);
        testService.addTestCase(addTestCaseRequest);
        return ResponseEntity.ok("success!!");
    }
}
