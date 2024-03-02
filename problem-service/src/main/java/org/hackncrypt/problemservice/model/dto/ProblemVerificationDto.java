package org.hackncrypt.problemservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class ProblemVerificationDto {
    @NotNull
    private List<TestCase> testCases;
    @NotNull
    private String sourceCode;
    @NotNull
    private int languageId;
}
