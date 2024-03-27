package org.hackncrypt.testservice.repositories;

import org.hackncrypt.testservice.models.entities.Test;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends MongoRepository<Test,String> {
    List<Test> findByProblemId(String problemId);
}
