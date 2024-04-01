package org.hackncrypt.testservice.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hackncrypt.testservice.enums.SubmissionStatus;
import org.hackncrypt.testservice.models.dto.testCases.AcceptedCase;
import org.hackncrypt.testservice.models.dto.testCases.RejectedCase;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RunAndTestResponse {
    private SubmissionStatus submissionStatus;
    private Double averageTime;
    private Double averageMemory;
    private List<RejectedCase> rejectedCases;
    private List<AcceptedCase> acceptedCases;
    private int totalTestCases;
}
