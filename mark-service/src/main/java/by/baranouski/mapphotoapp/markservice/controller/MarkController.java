package by.baranouski.mapphotoapp.markservice.controller;

import _by.baranouski.mapphotoapp.api.model.MarkPageDto;
import _by.baranouski.mapphotoapp.api.model.MarkRequestDto;
import _by.baranouski.mapphotoapp.api.model.MarkResponseDto;
import by.baranouski.mapphotoapp.api.MarksApi;
import by.baranouski.mapphotoapp.markservice.mapper.MarkMapper;
import by.baranouski.mapphotoapp.markservice.service.MarkService;
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
public class MarkController implements MarksApi {

    MarkService markService;
    MarkMapper markMapper;
    LinkAssembler linkAssembler;

    @Override
    @PreAuthorize("hasRole('ADMIN') " +
            "or (hasRole('USER') and @userAuthUtils.isSelf(#markRequestDto.userId))")
    public ResponseEntity<Void> createMark(MarkRequestDto markRequestDto) {
        var mark = markMapper.toMark(markRequestDto);
        return ResponseEntity.created(linkAssembler
                .getResponseEntity(markService.createMark(mark))
                .toUri()).build();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> deleteMark(String id) {
        var mark = markService.getAnyMark(id);
        markService.deleteMark(mark);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MarkPageDto> getAllMarks(Integer page, Integer size) {
        var result = markService.getMarks(page, size);
        var dtoList = markMapper.toResponseList(result.getContent());
        return ResponseEntity.ok(new MarkPageDto()
                .marks(dtoList)
                .page(result.getNumber())
                .total(result.getTotalElements()));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<MarkResponseDto> getMark(String id) {
        return ResponseEntity.ok(
                markMapper.toResponse(
                        markService.getMarkAccessibleForCurrentUser(id)));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<MarkPageDto> getUserMarks(String userId, Integer page, Integer size) {
        var result = markService.getMarksByUserId(userId, page, size);
        var dtoList = markMapper.toResponseList(result.getContent());
        return ResponseEntity.ok(new MarkPageDto()
                .marks(dtoList)
                .page(result.getNumber())
                .total(result.getTotalElements()));
    }
}
