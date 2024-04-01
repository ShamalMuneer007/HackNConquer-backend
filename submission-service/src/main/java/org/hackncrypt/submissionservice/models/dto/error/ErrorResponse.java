package org.hackncrypt.submissionservice.models.dto.error;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private LocalDate timestamp;
    private String path;
    private String message;
    private int status;
    private Map<String, List<String>> errors;
}
