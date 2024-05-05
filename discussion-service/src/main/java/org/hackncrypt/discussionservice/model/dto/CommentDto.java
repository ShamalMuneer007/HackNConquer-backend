package org.hackncrypt.discussionservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hackncrypt.discussionservice.model.entities.Comment;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private String comment;
    private String problemId;
    private String username;
    private Long userId;
    private String commentedAt;
    public CommentDto(Comment comment){
        this.comment = comment.getComment();
        this.username = comment.getUsername();
        this.problemId = comment.getProblemId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        this.commentedAt = comment.getCommentedAt() != null ? comment.getCommentedAt().format(formatter) : null;
    }
}
