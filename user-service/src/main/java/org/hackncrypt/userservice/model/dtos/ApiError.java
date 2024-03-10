package org.hackncrypt.userservice.model.dtos;

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
}
