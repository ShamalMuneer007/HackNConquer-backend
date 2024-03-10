package org.hackncrypt.problemservice.model.entities;

import lombok.*;
import org.hackncrypt.problemservice.model.dto.TestCases.TestCase;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collation = "problem")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Problem {
    @Id
    private String problemId;
    private long problemNo;
    private String problemName;
    private String problemDescription;
    private List<TestCase> testCases;
}


