package org.hackncrypt.testservice.models.dto.request;

import lombok.*;
import org.hackncrypt.testservice.models.dto.testCases.TestCase;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Validated
public class AddProblemRequest {
    private String problemName;
    private String description;
    private List<String> categories;
    private List<TestCase> testCases;
    private String driverCode;
    private String solutionTemplate;
    private String difficulty;
    private int languageId;
}
