package org.hackncrypt.discussionservice.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hackncrypt.discussionservice.model.dto.request.DiscussionCommentRequest;
import org.hackncrypt.discussionservice.model.dto.request.SubmitDiscussionRequest;
import org.hackncrypt.discussionservice.model.dto.response.ApiSuccessResponse;
import org.hackncrypt.discussionservice.services.CommentService;
import org.hackncrypt.discussionservice.services.DiscussionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/v1/user")
public class UserDiscussionController {
    private final DiscussionService discussionService;
    private final CommentService commentService;
    @PostMapping( "/submit-discussion")
    public ResponseEntity<ApiSuccessResponse> submitProblemDiscussion(@RequestBody @Valid SubmitDiscussionRequest submitDiscussionRequest, HttpServletRequest request){
        Long userId = Long.parseLong((String)request.getAttribute("userId"));
        discussionService.submitUserProblemDiscussion(submitDiscussionRequest,userId);
        ApiSuccessResponse response  = new ApiSuccessResponse("Discussion submitted successfully!!!", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/comment-discussion")
    public ResponseEntity<ApiSuccessResponse> addDiscussionComment(@RequestBody @Valid DiscussionCommentRequest discussionCommentRequest,
                                                                   HttpServletRequest request){
        Long userId = Long.parseLong((String)request.getAttribute("userId"));
        commentService.addDiscussionComment(discussionCommentRequest,userId);
        ApiSuccessResponse response  = new ApiSuccessResponse("Discussion submitted successfully!!!", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
