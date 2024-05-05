package org.hackncrypt.discussionservice.repositories;

import org.hackncrypt.discussionservice.model.entities.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion,Long> {
    List<Discussion> findByProblemId(String problemId);
}
