package org.hackncrypt.problemservice.repositories;

import org.hackncrypt.problemservice.model.dto.ProblemDto;
import org.hackncrypt.problemservice.model.entities.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository extends MongoRepository<Problem,String> {
    @Query("{ 'problemId' : { $in: ?0 } }")
    List<Problem> findAllInProblemId(List<String> problemIdList);
    Optional<Problem> findByProblemNo(long problemNo);

    boolean existsByProblemNameIgnoreCase(String problemName);

    Page<Problem> findAllByIsDeletedIsFalse(Pageable pageable);


}
