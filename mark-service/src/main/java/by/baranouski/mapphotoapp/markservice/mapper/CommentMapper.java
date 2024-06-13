package by.baranouski.mapphotoapp.markservice.mapper;

import _by.baranouski.mapphotoapp.api.model.CommentRequestDto;
import _by.baranouski.mapphotoapp.api.model.CommentResponseDto;
import by.baranouski.mapphotoapp.markservice.model.Comment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CommentMapper {
    Comment toComment(CommentRequestDto commentRequestDto);
    CommentResponseDto toResponse(Comment comment);
    List<CommentResponseDto> toResponseList(List<Comment> comments);
}
