package by.baranouski.mapphotoapp.markservice.controller;

import _by.baranouski.mapphotoapp.api.model.CommentPageDto;
import _by.baranouski.mapphotoapp.api.model.CommentRequestDto;
import _by.baranouski.mapphotoapp.api.model.CommentResponseDto;
import by.baranouski.mapphotoapp.api.CommentsApi;
import by.baranouski.mapphotoapp.markservice.mapper.CommentMapper;
import by.baranouski.mapphotoapp.markservice.service.CommentService;
import by.baranouski.mapphotoapp.markservice.util.LinkAssembler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CommentController implements CommentsApi {
    CommentService commentService;
    CommentMapper commentMapper;
    LinkAssembler linkAssembler;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> createComment(CommentRequestDto commentRequestDto) {
        var comment = commentService.createComment(commentMapper.toComment(commentRequestDto));
        return ResponseEntity
                .created(linkAssembler.getResponseEntity(comment).toUri())
                .build();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> deleteComment(String id) {
        commentService.deleteComment(commentService.getAnyComment(id));
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<CommentResponseDto> getComment(String id) {
        return ResponseEntity.ok(commentMapper.toResponse(commentService.getCommentAccessibleByCurrentUser(id)));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<CommentPageDto> getMarkComments(String markId, Integer page, Integer size) {
        var comments = commentService.getComments(markId, page, size);
        return ResponseEntity.ok(new CommentPageDto()
                .comments(commentMapper.toResponseList(comments.getContent()))
                .page(comments.getNumber())
                .total(comments.getTotalElements()));
    }
}
