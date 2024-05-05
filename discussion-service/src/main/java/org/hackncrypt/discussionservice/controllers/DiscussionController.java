package org.hackncrypt.discussionservice.controllers;

import lombok.RequiredArgsConstructor;
import org.hackncrypt.discussionservice.model.dto.DiscussionDto;
import org.hackncrypt.discussionservice.services.DiscussionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DiscussionController {
    private final DiscussionService discussionService;
    @GetMapping("/get-problem-discussions/{problemId}")
    public ResponseEntity<List<DiscussionDto>> getProblemDiscussions(@PathVariable String problemId){
        List<DiscussionDto> discussions = discussionService.findAllDiscussionsOfProblemId(problemId);
        return ResponseEntity.ok(discussions);
    }
}
