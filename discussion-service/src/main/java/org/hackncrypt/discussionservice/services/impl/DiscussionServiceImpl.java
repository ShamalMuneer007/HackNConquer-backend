package org.hackncrypt.discussionservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.discussionservice.config.rabbitMQ.MQConfig;
import org.hackncrypt.discussionservice.exceptions.DiscussionNotFoundException;
import org.hackncrypt.discussionservice.exceptions.firebase.FMCQueueException;
import org.hackncrypt.discussionservice.model.dto.DiscussionDto;
import org.hackncrypt.discussionservice.model.dto.request.CommentNotificationDto;
import org.hackncrypt.discussionservice.model.dto.request.SubmitDiscussionRequest;
import org.hackncrypt.discussionservice.model.entities.Comment;
import org.hackncrypt.discussionservice.model.entities.Discussion;
import org.hackncrypt.discussionservice.repositories.DiscussionRepository;
import org.hackncrypt.discussionservice.services.DiscussionService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscussionServiceImpl implements DiscussionService {
    private final DiscussionRepository discussionRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public List<DiscussionDto> findAllDiscussionsOfProblemId(String problemId) {
        List<Discussion> discussions = discussionRepository.findByProblemId(problemId);
        if(discussions.isEmpty())
            return null;
        return discussions.stream().map(DiscussionDto::new).toList();
    }

    @Override
    public void submitUserProblemDiscussion(SubmitDiscussionRequest submitDiscussionRequest, Long userId) {
        Discussion discussion = Discussion.builder()
                .title(submitDiscussionRequest.getTitle())
                .startedAt(LocalDateTime.now())
                .userId(userId)
                .username(submitDiscussionRequest.getUsername())
                .discussionContent(submitDiscussionRequest.getContent())
                .problemId(submitDiscussionRequest.getProblemId())
                .comments(new ArrayList<>())
                .build();
        discussionRepository.save(discussion);
    }

    @Override
    @Transactional
    public void addDiscussionComment(Comment comment, Long discussionId) {
        Optional<Discussion> discussionOptional = discussionRepository.findById(discussionId);
        Supplier<? extends RuntimeException> noDiscussionId =
                () -> new DiscussionNotFoundException("Discussion not found of ID: " + discussionId);
        Discussion discussion = discussionOptional.orElseThrow(noDiscussionId);
        discussion.getComments().add(comment);
        discussionRepository.save(discussion);
        sendCommentNotification(comment,discussion.getUserId(),discussion.getUsername());
    }
    private void sendCommentNotification(Comment comment,Long toUserId,String toUsername){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String commentedAt = comment.getCommentedAt().format(formatter);
        Long commentedUserId = comment.getUserId();
        String commentUsername = comment.getUsername();
        log.info("Sending notification to userID : {}",toUserId);
        try {
            rabbitTemplate.convertAndSend(MQConfig.WEB_FMC_EXCHANGE,
                    MQConfig.WEB_FMC_ROUTING_KEY, new CommentNotificationDto(Long.toString(toUserId), Long.toString(commentedUserId), comment.getComment(), commentedAt,toUsername,commentUsername));
        }
        catch (AmqpException exception){
            throw new FMCQueueException(exception);
        }
    }
}
