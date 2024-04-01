package org.hackncrypt.problemservice.model.dto.request;

import lombok.*;
import org.hackncrypt.problemservice.model.dto.testCases.TestCase;

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
