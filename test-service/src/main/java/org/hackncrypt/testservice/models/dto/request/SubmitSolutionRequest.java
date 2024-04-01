package org.hackncrypt.testservice.models.dto.request;

import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Validated
public class SubmitSolutionRequest {
    private String problemId;

    private Integer languageId;
    private String language;
    private String solutionCode;
    private String driverCode;
}
