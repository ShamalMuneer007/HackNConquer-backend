package org.hackncrypt.testservice.models.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
