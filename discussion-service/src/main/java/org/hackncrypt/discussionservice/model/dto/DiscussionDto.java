package org.hackncrypt.discussionservice.model.dto;

import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hackncrypt.discussionservice.model.entities.Comment;
import org.hackncrypt.discussionservice.model.entities.Discussion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscussionDto {
    private Long discussionId;
    private String title;
    private String discussionContent;
    private Long userId;
    private String username;
    private String problemId;
    private String startedAt;
    private List<CommentDto> comments;
    public DiscussionDto(Discussion discussion){
        this.title = discussion.getTitle();
        this.username = discussion.getUsername();
        this.userId = discussion.getUserId();
        this.problemId = discussion.getProblemId();
        this.discussionId = discussion.getDiscussionId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        this.startedAt = discussion.getStartedAt().format(formatter);

        this.discussionContent = discussion.getDiscussionContent();
        this.comments = discussion.getComments().stream().map(CommentDto::new).toList();
    }
}
