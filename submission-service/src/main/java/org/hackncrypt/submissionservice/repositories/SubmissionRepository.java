package org.hackncrypt.submissionservice.repositories;

import org.hackncrypt.submissionservice.enums.SubmissionStatus;
import org.hackncrypt.submissionservice.models.entities.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends MongoRepository<Submission,String> {
    List<Submission> findByProblemIdAndUserId(String problemNo, Long userId);

    List<Submission> findByProblemId(String problemId);

    List<Submission> findByUserIdAndSubmissionStatus(Long userId, SubmissionStatus submissionStatus);
}
