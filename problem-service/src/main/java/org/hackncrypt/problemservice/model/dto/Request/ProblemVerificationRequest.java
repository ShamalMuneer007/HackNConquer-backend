package org.hackncrypt.problemservice.model.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hackncrypt.problemservice.model.dto.TestCases.TestCase;
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
    private int languageId;
}
