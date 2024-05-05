package org.hackncrypt.discussionservice.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="comment_id")
    private Long commentId;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String comment;
    private String username;
    private String problemId;
    private LocalDateTime commentedAt;
    private Long userId;
}
