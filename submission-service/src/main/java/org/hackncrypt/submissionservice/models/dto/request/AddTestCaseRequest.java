package org.hackncrypt.submissionservice.models.dto.request;

import lombok.*;
import org.hackncrypt.submissionservice.models.dto.testCases.TestCase;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddTestCaseRequest {
    private String problemId;
    private List<TestCase> testCases;
}
