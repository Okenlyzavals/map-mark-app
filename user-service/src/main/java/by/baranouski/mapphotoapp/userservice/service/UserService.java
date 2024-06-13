package by.baranouski.mapphotoapp.userservice.service;

import by.baranouski.mapphotoapp.userservice.client.AuthProviderClient;
import by.baranouski.mapphotoapp.userservice.exception.EntityNotFoundException;
import by.baranouski.mapphotoapp.userservice.exception.UserAlreadyExistsException;
import by.baranouski.mapphotoapp.userservice.model.Invitation;
import by.baranouski.mapphotoapp.userservice.model.Role;
import by.baranouski.mapphotoapp.userservice.model.Status;
import by.baranouski.mapphotoapp.userservice.model.User;
import by.baranouski.mapphotoapp.userservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService {

    UserRepository userRepository;
    AuthProviderClient authProviderClient;


    public User createUserFromInvitation(User user, Invitation invitation, String password) {
        log.info("Creating {}-type user from invitation for email '{}' username '{}'", invitation.getRole(), user.getEmail(), user.getUsername());
        return createUser(user.setRole(invitation.getRole()), password);
    }

    public User createBasicUser(User user, String password) {
        log.info("Creating {}-type user for email '{}' username '{}'", Role.USER, user.getEmail(), user.getUsername());
        return createUser(user.setRole(Role.USER), password);
    }

    private User createUser(User user, String password) {
        if (Boolean.TRUE.equals(userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername()))) {
            throw new UserAlreadyExistsException(
                    String.format("User with email %s or username %s already exists", user.getEmail(), user.getUsername()));
        }
        user.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC))
                .setStatus(Status.ACTIVE);
        var id = authProviderClient.createUser(user, password);
        var stored = userRepository.save(user.setId(id));
        log.info("Successfully created user for email '{}' with id '{}'", stored.getEmail(), stored.getId());
        return stored;
    }

    public void blockUser(String id) {
        log.info("Attempting to block user with id '{}'", id);
        var user = getUser(id);
        if (user.getStatus() == Status.BLOCKED) {
            log.info("User with id '{}' is already blocker", id);
            return;
        }
        if (user.getRole() == Role.ADMIN) {
            throw new AccessDeniedException("Blocking administrator accounts is not permitted!");
        }
        authProviderClient.blockUser(user);
        userRepository.save(user.setStatus(Status.BLOCKED));
        log.info("Successfully blocked user with id '{}'", id);
    }

    public void unblockUser(String id) {
        log.info("Attempting to unblock user with id '{}'", id);
        var user = getUser(id);
        if (user.getStatus() == Status.ACTIVE) {
            log.info("User with id '{}' is not blocked", id);
            return;
        }
        authProviderClient.unblockUser(user);
        userRepository.save(user.setStatus(Status.ACTIVE));
        log.info("Successfully unblocked user with id '{}'", id);
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findUser(String id) {
        return getUser(id);
    }

    private User getUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user for id " + id));
    }
}
