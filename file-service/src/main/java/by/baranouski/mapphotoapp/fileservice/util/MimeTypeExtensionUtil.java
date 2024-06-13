package by.baranouski.mapphotoapp.fileservice.util;

import by.baranouski.mapphotoapp.fileservice.exception.MimeTypeMismatchException;
import by.baranouski.mapphotoapp.fileservice.model.FileType;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

public class MimeTypeExtensionUtil {

    private static final Map<FileType, List<String>> FILE_TYPE_MIME_TYPE_MAP = Map.of(
            FileType.IMAGE, List.of(
                    MediaType.IMAGE_JPEG_VALUE,
                    MediaType.IMAGE_GIF_VALUE,
                    MediaType.IMAGE_PNG_VALUE));

    private static final Map<String, String> MIME_TYPE_EXTENSION_MAP = Map.of(
            MediaType.IMAGE_JPEG_VALUE, "jpg",
            MediaType.IMAGE_GIF_VALUE, "gif",
            MediaType.IMAGE_PNG_VALUE, "png");


    public static String getExtension(FileType fileType, String mimeType){
        if (FILE_TYPE_MIME_TYPE_MAP.containsKey(fileType) && FILE_TYPE_MIME_TYPE_MAP.get(fileType).contains(mimeType)){
            return MIME_TYPE_EXTENSION_MAP.get(mimeType);
        }
        throw new MimeTypeMismatchException(String.format("Provided %s for %s type of file", mimeType, fileType));
    }

    private MimeTypeExtensionUtil() {}
}
