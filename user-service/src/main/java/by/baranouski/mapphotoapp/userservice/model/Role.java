package by.baranouski.mapphotoapp.userservice.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum Role {
    ADMIN("ADMIN"),
    USER("USER");

    String value;
}
