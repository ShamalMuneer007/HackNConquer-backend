package org.hackncrypt.problemservice.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class ChangeProblemAcceptanceRateRequest {
    @NotNull
    private String problemId;
    @NotNull
    private float acceptanceRate;
}
