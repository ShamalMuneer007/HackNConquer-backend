package org.hackncrypt.problemservice.model.dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Judge0Request {
    @JsonProperty
    private int language_id;
    @JsonProperty
    private String source_code;
    @JsonProperty
    private String stdin;
}
