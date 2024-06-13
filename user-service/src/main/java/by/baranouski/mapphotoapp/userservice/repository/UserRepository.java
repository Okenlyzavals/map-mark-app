package by.baranouski.mapphotoapp.userservice.repository;

import by.baranouski.mapphotoapp.userservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Boolean existsByEmailOrUsername(String email, String username);

    Boolean existsByEmail(String email);
}
