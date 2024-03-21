package org.hackncrypt.problemservice.model.dto;

import lombok.*;
import org.hackncrypt.problemservice.enums.Difficulty;
import org.hackncrypt.problemservice.model.dto.testCases.TestCase;
import org.hackncrypt.problemservice.model.entities.Category;
import org.hackncrypt.problemservice.model.entities.Problem;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDto {
    private String problemId;
    private Long problemNo;
    private String problemName;
    private String description;
    private String driverCode;
    private Integer languageId;
    private String difficulty;
    private String solutionTemplate;
    private List<Category> categories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TestCase> testCases;
    public ProblemDto(Problem problem){
        this.createdAt = problem.getCreatedAt();
        this.description = problem.getDescription();
        this.problemId = problem.getProblemId();
        this.testCases = problem.getTestCases();
        this.categories = problem.getCategories();
        this.difficulty = problem.getDifficulty().name();
        this.driverCode = problem.getDriverCode();
        this.problemName = problem.getProblemName();
        this.languageId = problem.getLanguageId();
        this.solutionTemplate = problem.getSolutionTemplate();
        this.updatedAt = problem.getUpdatedAt();
        this.problemNo = problem.getProblemNo();
    }
}
