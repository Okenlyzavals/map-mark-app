package by.baranouski.mapphotoapp.fileservice.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
@Builder(toBuilder = true)
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileData {
    @Id
    String id;
    String fileName;
    FileType fileType;
    LocalDateTime createdAt;
    String createdBy;
}
