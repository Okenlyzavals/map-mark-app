package by.baranouski.mapphotoapp.userservice.repository;

import by.baranouski.mapphotoapp.userservice.model.Invitation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends MongoRepository<Invitation, String> {

}
