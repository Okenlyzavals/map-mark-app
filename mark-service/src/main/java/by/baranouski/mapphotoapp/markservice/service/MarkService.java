package by.baranouski.mapphotoapp.markservice.service;

import by.baranouski.mapphotoapp.markservice.exception.EntityNotFoundException;
import by.baranouski.mapphotoapp.markservice.model.Mark;
import by.baranouski.mapphotoapp.markservice.repository.MarkRepository;
import by.baranouski.mapphotoapp.markservice.util.UserAuthUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MarkService {
    MarkRepository markRepository;

    public Page<Mark> getMarks(int page, int size) {
        return markRepository.findAll(PageRequest.of(page, size, Sort.Direction.DESC, Mark.Fields.createdAt));
    }

    public Page<Mark> getMarksByUserId(String userId, int page, int size) {
        log.info("Attempting to retrieve marks for userId {}", userId);
        var pageable = PageRequest.of(page, size, Sort.Direction.DESC, Mark.Fields.createdAt);
        if (Boolean.TRUE.equals(UserAuthUtils.isAdmin())) {
            log.info("Retrieving all marks for userId {}", userId);
            return markRepository.findAllByUserId(userId, pageable);
        } else if (Boolean.TRUE.equals(UserAuthUtils.isSelf(userId))){
            log.info("Retrieving non-deleted marks for userId {}", userId);
            return markRepository.findAllByUserIdAndIsDeleted(userId, false, pageable);
        } else {
            throw new AccessDeniedException(String.format("User %s cannot access marks of another user %s", UserAuthUtils.getCurrentUserId(), userId));
        }
    }

    public void checkMarkAccessibleByCurrentUser(String id){
        checkMarkAccessibleByCurrentUser(getAnyMark(id));
    }

    private void checkMarkAccessibleByCurrentUser(Mark mark){
        if (Boolean.FALSE.equals(UserAuthUtils.isAdmin())
                && (Boolean.FALSE.equals(UserAuthUtils.isSelf(mark.getUserId())) || mark.getIsDeleted())) {
            throw new AccessDeniedException(String.format("User %s cannot access mark %s", UserAuthUtils.getCurrentUserId(), mark.getId()));
        }
    }

    public Mark getMarkAccessibleForCurrentUser(String id) {
        var mark = getAnyMark(id);
        checkMarkAccessibleByCurrentUser(mark);
        return mark;
    }

    public Mark getAnyMark(String id) {
        return markRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find mark with id " + id));
    }

    public Mark createMark(Mark mark) {
        log.info("Attempting to create mark for userId {}", mark.getUserId());
        mark.setId(UUID.randomUUID().toString())
                .setCreatedAt(LocalDateTime.now(ZoneOffset.UTC))
                .setIsDeleted(false)
                .setCreatedBy(UserAuthUtils.getCurrentUserId());
        var res = markRepository.save(mark);
        log.info("Successfully created mark for userId '{}' with id '{}'", res.getUserId(), res.getId());
        return res;
    }

    public void deleteMark(Mark mark) {
        log.info("Attempting to delete mark by id {}", mark.getId());
        if (Boolean.TRUE.equals(UserAuthUtils.isAdmin()) || mark.getUserId().compareTo(UserAuthUtils.getCurrentUserId()) == 0) {
            if (Boolean.FALSE.equals(mark.getIsDeleted())) {
                markRepository.markDeletedById(mark.getId());
                log.info("Successfully deleted mark by id {}", mark.getId());
            } else {
                log.info("Mark with id {} is already deleted, skipping", mark.getId());
            }
        } else {
            throw new AccessDeniedException(String.format(
                    "User '%s' is not allowed to modify mark '%s'", UserAuthUtils.getCurrentUserId(), mark.getId()));
        }
    }

}
