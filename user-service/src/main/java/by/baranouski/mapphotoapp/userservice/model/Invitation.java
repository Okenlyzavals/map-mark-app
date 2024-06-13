package by.baranouski.mapphotoapp.userservice.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
@NoArgsConstructor
@Accessors(chain = true)
@FieldNameConstants
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invitation {
    @Id
    String email;
    Role role;
    LocalDateTime invitedDate;
    String invitedBy;
}
