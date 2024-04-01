package org.hackncrypt.submissionservice.models.entities;

import lombok.*;
import org.hackncrypt.submissionservice.enums.SubmissionStatus;
import org.hackncrypt.submissionservice.models.dto.testCases.AcceptedCase;
import org.hackncrypt.submissionservice.models.dto.testCases.RejectedCase;
import org.hackncrypt.submissionservice.models.dto.testCases.TestCase;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("submission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Submission {
    @Id
    private String submissionId;
    private Long userId;
    private String problemId;
    private Double averageTime;
    private Double averageMemory;
    private LocalDateTime submittedAt;
    private String solutionCode;
    private List<AcceptedCase> acceptedCases;
    private List<RejectedCase> rejectedCases;
    private int totalTestCases;
    private SubmissionStatus submissionStatus;

}
