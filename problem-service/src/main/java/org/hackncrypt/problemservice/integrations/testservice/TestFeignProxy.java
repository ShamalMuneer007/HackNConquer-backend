package org.hackncrypt.problemservice.integrations.testservice;

import org.hackncrypt.problemservice.model.dto.request.AddTestCaseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "test",path = "/test")
public interface TestFeignProxy {

    @PostMapping("/api/v1/admin/add-testcase")
    ResponseEntity<String> addProblemTestCases(@RequestBody  AddTestCaseRequest addTestCaseRequest);
}