package by.baranouski.mapphotoapp.markservice.service;

import by.baranouski.mapphotoapp.markservice.exception.EntityNotFoundException;
import by.baranouski.mapphotoapp.markservice.model.Comment;
import by.baranouski.mapphotoapp.markservice.model.Mark;
import by.baranouski.mapphotoapp.markservice.repository.CommentRepository;
import by.baranouski.mapphotoapp.markservice.util.UserAuthUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static by.baranouski.mapphotoapp.markservice.util.UserAuthUtils.isAdmin;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CommentService {
    CommentRepository commentRepository;
    MarkService markService;

    public Page<Comment> getComments(String markId, int page, int size) {
        log.info("Attempting to get comments for mark '{}'", markId);
        markService.checkMarkAccessibleByCurrentUser(markId);
        var pageable = PageRequest.of(page, size, Sort.Direction.DESC, Mark.Fields.createdAt);
        if (Boolean.TRUE.equals(isAdmin())) {
            log.info("Retrieving all comments for mark '{}'", markId);
            return commentRepository.findCommentsByMarkId(markId, pageable);
        } else {
            log.info("Retrieving non-deleted comments for mark '{}'", markId);
            return commentRepository.findCommentsByMarkIdAndIsDeleted(markId, false, pageable);
        }
    }


    public Comment getCommentAccessibleByCurrentUser(String id) {
        log.info("Attempting to get comment by id '{}' with accessibility check", id);
        var comment = getAnyComment(id);
        markService.checkMarkAccessibleByCurrentUser(comment.getMarkId());
        if (Boolean.FALSE.equals(isAdmin())
            && (Boolean.TRUE.equals(comment.getIsDeleted()))) {
            throw new AccessDeniedException(String.format(
                    "User '%s' is not allowed to see comment '%s'", UserAuthUtils.getCurrentUserId(), comment.getId()));
        }
        return comment;
    }

    public Comment getAnyComment(String id) {
        log.info("Attempting to get comment by id '{}'", id);
        return commentRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find comment with id " + id));
    }

    public Comment createComment(Comment comment) {
        log.info("Attempting to create a comment for mark '{}', userId='{}'", comment.getMarkId(), UserAuthUtils.getCurrentUserId());
        markService.checkMarkAccessibleByCurrentUser(comment.getMarkId());
        comment.setId(UUID.randomUUID().toString())
                .setCreatedAt(LocalDateTime.now(ZoneOffset.UTC))
                .setUserId(UserAuthUtils.getCurrentUserId())
                .setIsDeleted(false);
        var res =  commentRepository.save(comment);
        log.info("Successfully created a comment for mark '{}', comment id='{}'", res.getMarkId(), res.getId());
        return res;
    }

    public void deleteComment(Comment comment) {
        log.info("Attempting to delete a comment by id '{}'", comment.getId());
        if (Boolean.TRUE.equals(isAdmin()) || comment.getUserId().compareTo(UserAuthUtils.getCurrentUserId()) == 0) {
            if (Boolean.FALSE.equals(comment.getIsDeleted())) {
                commentRepository.markDeletedById(comment.getId());
                log.info("Successfully deleted a comment by id='{}'", comment.getId());
            } else {
                log.info("Comment with id '{}' is already deleted, skipping", comment.getId());
            }
        } else {
            throw new AccessDeniedException(String.format(
                    "User '%s' is not allowed to modify comment '%s'", UserAuthUtils.getCurrentUserId(), comment.getId()));
        }
    }

}
