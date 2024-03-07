package org.hackncrypt.problemservice.repositories;

import org.hackncrypt.problemservice.model.entities.Problem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends MongoRepository<Problem,String> {
}
