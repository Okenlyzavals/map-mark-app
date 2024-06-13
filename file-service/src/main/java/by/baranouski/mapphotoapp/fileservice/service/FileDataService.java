package by.baranouski.mapphotoapp.fileservice.service;

import by.baranouski.mapphotoapp.fileservice.client.FileStorageClient;
import by.baranouski.mapphotoapp.fileservice.exception.EntityNotFoundException;
import by.baranouski.mapphotoapp.fileservice.exception.FileProcessingException;
import by.baranouski.mapphotoapp.fileservice.model.FileData;
import by.baranouski.mapphotoapp.fileservice.model.FileType;
import by.baranouski.mapphotoapp.fileservice.repository.FileDataRepository;
import by.baranouski.mapphotoapp.fileservice.util.UserAuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FileDataService {
    FileDataRepository fileDataRepository;
    FileStorageClient fileStorageClient;

    public String uploadFile(FileType type, MultipartFile multipartFile) {
        log.info("Attempting to upload '{}' file '{}' from user '{}'", type, multipartFile.getOriginalFilename(), UserAuthUtil.getCurrentUserId());
        var fileData = FileData.builder()
                .id(UUID.randomUUID().toString())
                .fileType(type)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .createdBy(UserAuthUtil.getCurrentUserId())
                .build();
        fileData = fileStorageClient.save(fileData, multipartFile);
        try {
            fileDataRepository.insert(fileData);
            log.info("Successfully uploaded '{}' file with id {}", multipartFile.getOriginalFilename(), fileData.getId());
            return fileData.getId();
        } catch (Exception e){
            throw new FileProcessingException("Failed ot upload file", e);
        }
    }

    public byte[] downloadFile(String id){
        log.info("Attempting to download file by id '{}'", id);
        var fileData = fileDataRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Could not find file with id "+id));
        var file =  fileStorageClient.download(fileData);
        log.info("Successfully downloaded file with id '{}'", id);
        return file;
    }

    public FileData findById(String id){
        return fileDataRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Could not find file with id "+id));
    }

}
