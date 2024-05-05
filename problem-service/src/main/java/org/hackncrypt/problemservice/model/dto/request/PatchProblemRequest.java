package org.hackncrypt.problemservice.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.hackncrypt.problemservice.model.dto.testCases.TestCase;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchProblemRequest {
    @Size(min = 5)
    private String name;
    @Size(min = 10)
    private String description;
    private List<String> categories;
    private List<TestCase> testCases;
    private String driverCode;
    private String solutionTemplate;
    private String difficulty;
    private Integer languageId;
}
