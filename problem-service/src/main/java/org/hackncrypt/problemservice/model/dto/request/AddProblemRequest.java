package org.hackncrypt.problemservice.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hackncrypt.problemservice.model.dto.testCases.TestCase;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Validated
public class AddProblemRequest {
    @NotNull
    @Size(min = 5)
    private String problemName;
    @NotNull
    @Size(min = 10)
    private String description;
    @NotNull
    private List<String> categories;
    @NotNull
    private List<TestCase> testCases;
    @NotNull
    private String driverCode;
    @NotNull
    private String solutionTemplate;
    @NotNull
    private String difficulty;
    private int languageId;
}
