package org.hackncrypt.problemservice.model.dto.Response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hackncrypt.problemservice.model.dto.SubmissionStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonAutoDetect
public class JudgeSubmissionResponse {
    @JsonProperty
    private String stdout;
    @JsonProperty
    private String stderr;
    @JsonProperty
    private String message;
    @JsonProperty
    private String time;
    @JsonProperty
    private String memory;
    @JsonProperty
    private String compile_output;
    @JsonProperty
    private SubmissionStatus status = new SubmissionStatus();
}
