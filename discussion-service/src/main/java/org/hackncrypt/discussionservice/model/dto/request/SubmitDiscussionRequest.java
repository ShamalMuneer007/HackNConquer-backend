package org.hackncrypt.discussionservice.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class SubmitDiscussionRequest {
    @NotNull
    private String username;
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    private String problemId;
}
