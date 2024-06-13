package by.baranouski.mapphotoapp.fileservice.client.impl;

import by.baranouski.mapphotoapp.fileservice.model.FileType;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = "file-storage-client",
        url = "${storage-server.uri}")
public interface FileServerStorageFeignClient {

    @PutMapping(
            value = "/{type}/{name}.{extension}",
            consumes = "multipart/form-data",
            headers = "Authorization=Bearer ${storage-server.write-key}")
    String save(
            @PathVariable("name") String fileName,
            @PathVariable("extension") String extension,
            @PathVariable("type") FileType type,
            @RequestPart("file") MultipartFile fileContent);

    @GetMapping(
            value = "/{type}/{name}",
            headers = "Authorization=Bearer ${storage-server.read-key}")
    Response download(@PathVariable("name") String fileName, @PathVariable("type") FileType type);
}
