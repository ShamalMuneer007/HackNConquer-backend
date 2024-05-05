package org.hackncrypt.discussionservice.repositories;

import org.hackncrypt.discussionservice.model.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
}
