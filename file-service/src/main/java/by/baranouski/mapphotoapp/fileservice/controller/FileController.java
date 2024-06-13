package by.baranouski.mapphotoapp.fileservice.controller;

import _by.baranouski.mapphotoapp.api.model.FileTypeDto;
import by.baranouski.mapphotoapp.api.FilesApi;
import by.baranouski.mapphotoapp.fileservice.mapper.FileTypeMapper;
import by.baranouski.mapphotoapp.fileservice.service.FileDataService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FileController implements FilesApi {

    FileDataService fileDataService;
    FileTypeMapper fileTypeMapper;

    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> uploadFile(FileTypeDto type, MultipartFile file) {
        var fileType = fileTypeMapper.toFileType(type);
        var id = fileDataService.uploadFile(fileType, file);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<byte[]> downloadFile(String fileId) {
        var fileData = fileDataService.findById(fileId);
        var resource = fileDataService.downloadFile(fileId);
        var headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileData.getFileName()).build());
        headers.setContentType(MediaTypeFactory.getMediaType(fileData.getFileName()).orElse(MediaType.APPLICATION_OCTET_STREAM));
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(resource);
    }
}
