package org.hackncrypt.testservice.models.dto.request;

import lombok.*;
import org.hackncrypt.testservice.models.dto.testCases.TestCase;

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
