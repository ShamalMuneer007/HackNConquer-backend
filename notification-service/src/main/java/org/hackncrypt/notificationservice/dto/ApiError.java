package org.hackncrypt.notificationservice.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ApiError {
    private String message;
    private LocalDate timeStamp;
    private int status;
    private String requestPath;
    private String trace;
}
