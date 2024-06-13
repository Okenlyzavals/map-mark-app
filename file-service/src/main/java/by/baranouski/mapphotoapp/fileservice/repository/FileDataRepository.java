package by.baranouski.mapphotoapp.fileservice.repository;

import by.baranouski.mapphotoapp.fileservice.model.FileData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDataRepository extends MongoRepository<FileData,String> {

}
