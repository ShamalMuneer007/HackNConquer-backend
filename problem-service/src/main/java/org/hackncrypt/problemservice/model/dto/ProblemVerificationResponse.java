package org.hackncrypt.problemservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hackncrypt.problemservice.enums.SubmissionStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemVerificationResponse {
    @JsonProperty   
    private String message;
    @JsonProperty
    private String status;
    @JsonProperty
    private List<AcceptedCase> acceptedCases;
    @JsonProperty
    private List<RejectedCase> rejectedCases;
}
