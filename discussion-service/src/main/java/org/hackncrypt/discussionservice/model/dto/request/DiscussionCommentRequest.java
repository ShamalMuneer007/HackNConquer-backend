package org.hackncrypt.discussionservice.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Validated
@ToString
public class DiscussionCommentRequest {
    @NotNull
    private Long discussionId;
    @NotNull
    private String comment;
    @NotNull
    private String username;
    @NotNull
    private String problemId;
}
