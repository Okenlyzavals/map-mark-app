package by.baranouski.mapphotoapp.fileservice.client.impl;

import by.baranouski.mapphotoapp.fileservice.client.FileStorageClient;
import by.baranouski.mapphotoapp.fileservice.exception.EntityNotFoundException;
import by.baranouski.mapphotoapp.fileservice.exception.FileProcessingException;
import by.baranouski.mapphotoapp.fileservice.model.FileData;
import by.baranouski.mapphotoapp.fileservice.util.MimeTypeExtensionUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FileStorageClientLocal implements FileStorageClient {

    FileServerStorageFeignClient fileServerStorageFeignClient;

    @Override
    public FileData save(FileData fileData, MultipartFile fileContent) {
        try {
            var mimeType = new Tika().detect(fileContent.getBytes());
            var extension = MimeTypeExtensionUtil.getExtension(fileData.getFileType(), mimeType);
            fileServerStorageFeignClient.save(fileData.getId(), extension, fileData.getFileType(), fileContent);
            log.info("Successfully stored {} file {}.{}", fileData.getFileType(), fileData.getId(), extension);
            return fileData.toBuilder().fileName(String.format("%s.%s", fileData.getId(), extension)).build();
        } catch (IOException e){
            log.error("Failed to process {} file {}", fileData.getFileType(), fileData.getId(), e);
            throw new FileProcessingException("Could not process file", e);
        }
    }

    @Override
    public byte[] download(FileData fileData) {
        try (var resp = fileServerStorageFeignClient.download(fileData.getFileName(), fileData.getFileType())) {
            return switch (HttpStatus.valueOf(resp.status())) {
                case OK -> removeCorruptingData(resp.body().asInputStream().readAllBytes());
                case NOT_FOUND -> throw new EntityNotFoundException("Could not find file " + fileData.getFileName());
                default -> throw new FileProcessingException("Could not process file during download: " + fileData.getFileName());
            };
        } catch (IOException e){
            log.error("Failed to process {} file {} while downloading", fileData.getFileType(), fileData.getId(), e);
            throw new FileProcessingException("Could not process file", e);
        }
    }

    /**
     * File server used for local mocking corrupts images with some own data in the begginning and at the end.
     * It did not affect jpgs, but corrupted pngs. So I had to deduce a workaround.

     */
    private byte[] removeCorruptingData(byte[] uncleanData){
        byte lineBreakOne = 13;
        byte lineBreakTwo = 10;
        int encounteredBreaks = 0;
        int fifthBreakIndex = 0;
        int secondFromLastBreakIndex = 0;

        int index = 1;
        while (fifthBreakIndex == 0){
            if (uncleanData[index] == lineBreakTwo && uncleanData[index-1]==lineBreakOne){
                encounteredBreaks++;
            }
            if (encounteredBreaks == 5){
                fifthBreakIndex = index;
            }
            index++;
        }

        index = uncleanData.length-2;
        encounteredBreaks = 0;
        while (secondFromLastBreakIndex == 0){
            if (uncleanData[index] == lineBreakOne && uncleanData[index+1]==lineBreakTwo){
                encounteredBreaks++;
            }
            if (encounteredBreaks == 2){
                secondFromLastBreakIndex = index;
            }
            index--;
        }
        return Arrays.copyOfRange(uncleanData, fifthBreakIndex+1, secondFromLastBreakIndex );

    }
}
