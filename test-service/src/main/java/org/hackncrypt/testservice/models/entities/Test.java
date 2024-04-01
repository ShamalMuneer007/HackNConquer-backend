package org.hackncrypt.testservice.models.entities;

import lombok.*;
import org.hackncrypt.testservice.models.dto.testCases.TestCase;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Test {
    @Id
    private String test_id;
    private List<TestCase> testCases;
    private String problemId;
}
