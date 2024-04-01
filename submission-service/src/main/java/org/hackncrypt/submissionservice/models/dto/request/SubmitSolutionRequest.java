package org.hackncrypt.submissionservice.models.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Validated
public class SubmitSolutionRequest {
    @NotNull
    private String problemId;
    @NotNull
    private Integer languageId;
    private String language;
    @NotNull
    private String solutionCode;
    @NotNull
    private Integer problemLevel;
    @NotNull
    private String driverCode;
}
