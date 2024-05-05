package org.hackncrypt.discussionservice.services;

import org.hackncrypt.discussionservice.model.dto.DiscussionDto;
import org.hackncrypt.discussionservice.model.dto.request.SubmitDiscussionRequest;
import org.hackncrypt.discussionservice.model.entities.Comment;

import java.util.List;

public interface DiscussionService {
    List<DiscussionDto> findAllDiscussionsOfProblemId(String problemId);

    void submitUserProblemDiscussion(SubmitDiscussionRequest submitDiscussionRequest, Long userId);

    void addDiscussionComment(Comment comment, Long discussionId);
}
