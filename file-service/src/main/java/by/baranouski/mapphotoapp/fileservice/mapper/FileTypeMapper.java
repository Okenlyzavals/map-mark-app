package by.baranouski.mapphotoapp.fileservice.mapper;

import _by.baranouski.mapphotoapp.api.model.FileTypeDto;
import by.baranouski.mapphotoapp.fileservice.model.FileType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileTypeMapper {
    FileType toFileType(FileTypeDto fileTypeDto);
}
