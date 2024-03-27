package org.hackncrypt.submissionservice.models.dto;

import lombok.*;
import org.hackncrypt.submissionservice.models.entities.Submission;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionDto {
    private Double averageMemory;
    private Double averageTime;
    private String submissionStatus;

    public SubmissionDto(Submission submission) {
        this.averageMemory = submission.getAverageMemory();
        this.averageTime = submission.getAverageTime();
        this.submissionStatus = submission.getSubmissionStatus().name().replace('_',' ');
    }
}
