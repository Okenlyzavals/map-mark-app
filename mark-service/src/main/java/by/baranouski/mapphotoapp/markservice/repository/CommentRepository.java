package by.baranouski.mapphotoapp.markservice.repository;

import by.baranouski.mapphotoapp.markservice.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    Page<Comment> findCommentsByMarkIdAndIsDeleted(String markId, Boolean isDeleted, Pageable pageable);
    Page<Comment> findCommentsByMarkId(String markId, Pageable pageable);
    @Query("{ 'id' : ?0 }")
    @Update("{ '$set' : { 'isDeleted' : true } }")
    void markDeletedById(String id);
}
