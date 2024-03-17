package org.hackncrypt.problemservice.repositories;

import org.hackncrypt.problemservice.model.entities.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category,String> {

    @Query("{ 'categoryName' : { $in : ?0 } }")
    List<Category> findAllByCategoryNameIn(List<String> categoryNames);

    List<Category> findAllByIsDeletedIsFalse();

    boolean existsByCategoryName(String categoryName);
}
