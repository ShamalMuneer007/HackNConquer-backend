package org.hackncrypt.problemservice.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hackncrypt.problemservice.model.dto.testCases.TestCase;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class ProblemVerificationRequest {
    @NotNull
    private List<TestCase> testCases;
    @NotNull
    private String sourceCode;
    @NotNull
    private Integer languageId;
}
