package org.hackncrypt.discussionservice.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Discussion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="discussion_id")
    private Long discussionId;
    private String title;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String discussionContent;
    private Long userId;
    private String username;
    private String problemId;
    private LocalDateTime startedAt;
    @OneToMany
    private List<Comment> comments;
}
