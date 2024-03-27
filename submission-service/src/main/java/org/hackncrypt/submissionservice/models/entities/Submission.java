package org.hackncrypt.submissionservice.models.entities;

import lombok.*;
import org.hackncrypt.submissionservice.enums.SubmissionStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private SubmissionStatus submissionStatus;

}
