package org.hackncrypt.problemservice.model.dto.response;

import lombok.*;
import org.hackncrypt.problemservice.model.dto.ProblemDto;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllProblemResponse {
    private Page<ProblemDto> problems;
    private int status;
}
