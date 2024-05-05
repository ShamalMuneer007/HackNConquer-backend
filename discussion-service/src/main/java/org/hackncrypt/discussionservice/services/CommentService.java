package org.hackncrypt.discussionservice.services;

import org.hackncrypt.discussionservice.model.dto.request.DiscussionCommentRequest;

public interface CommentService {
    void addDiscussionComment(DiscussionCommentRequest discussionCommentRequest, Long userId);
}
