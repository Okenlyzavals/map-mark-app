package by.baranouski.mapphotoapp.fileservice.client;

import by.baranouski.mapphotoapp.fileservice.model.FileData;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageClient {
    FileData save(FileData fileData, MultipartFile fileContent);
    byte[] download(FileData fileData);
}
