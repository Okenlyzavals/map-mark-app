package by.baranouski.mapphotoapp.markservice.repository;

import by.baranouski.mapphotoapp.markservice.model.Mark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkRepository extends MongoRepository<Mark, String> {

    Page<Mark> findAllByUserId(String userId, Pageable pageable);
    Page<Mark> findAllByUserIdAndIsDeleted(String userId, Boolean isDeleted, Pageable pageable);
    @Query("{ 'id' : ?0 }")
    @Update("{ '$set' : { 'isDeleted' : true } }")
    void markDeletedById(String id);
}
