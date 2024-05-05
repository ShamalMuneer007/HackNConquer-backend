package org.hackncrypt.discussionservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.discussionservice.model.dto.request.DiscussionCommentRequest;
import org.hackncrypt.discussionservice.model.entities.Comment;
import org.hackncrypt.discussionservice.repositories.CommentRepository;
import org.hackncrypt.discussionservice.services.CommentService;
import org.hackncrypt.discussionservice.services.DiscussionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final DiscussionService discussionService;

    @Override
    @Transactional
    public void addDiscussionComment(DiscussionCommentRequest discussionCommentRequest, Long userId) {
        log.info("Comment Request Object {}",discussionCommentRequest);
        Comment comment = Comment.builder()
                .problemId(discussionCommentRequest.getProblemId())
                .comment(discussionCommentRequest.getComment())
                .username(discussionCommentRequest.getUsername())
                .userId(userId)
                .commentedAt(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
        discussionService.addDiscussionComment(comment,discussionCommentRequest.getDiscussionId());
    }
}
