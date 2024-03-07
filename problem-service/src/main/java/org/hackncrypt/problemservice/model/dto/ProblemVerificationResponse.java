package org.hackncrypt.problemservice.model.dto;

import lombok.*;
import org.hackncrypt.problemservice.enums.SubmissionStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemVerificationResponse {
    private String message;
    private String status;
    private List<AcceptedCase> acceptedCases;
    private List<RejectedCase> rejectedCases;
}
