package org.hackncrypt.problemservice.model.entities;

import lombok.*;
import org.hackncrypt.problemservice.enums.Difficulty;
import org.hackncrypt.problemservice.model.dto.ProblemExample;
import org.hackncrypt.problemservice.model.dto.testCases.TestCase;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document("problem")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Problem {
    @Id
    private String problemId;
    @Field("problemNo")
    private Long problemNo;
    private String description;
    private String problemName;
    private String driverCode;
    private Integer languageId;
    private Difficulty difficulty;
    private String solutionTemplate;
    private List<Category> categories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProblemExample> examples;
    private List<TestCase> testCases;
    private Float acceptanceRate;
    private Integer level;
    private boolean isDeleted;
}


