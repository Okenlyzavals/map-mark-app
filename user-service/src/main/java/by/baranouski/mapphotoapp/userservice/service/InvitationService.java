package by.baranouski.mapphotoapp.userservice.service;

import by.baranouski.mapphotoapp.userservice.exception.EntityNotFoundException;
import by.baranouski.mapphotoapp.userservice.exception.UserAlreadyInvitedException;
import by.baranouski.mapphotoapp.userservice.model.Invitation;
import by.baranouski.mapphotoapp.userservice.model.Role;
import by.baranouski.mapphotoapp.userservice.repository.InvitationRepository;
import by.baranouski.mapphotoapp.userservice.util.UserAuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InvitationService {
    InvitationRepository invitationRepository;
    UserService userService;

    public void inviteUser(String email, Role role) {
        log.info("Trying to invite {}-type user by email '{}'", role, email);
        if (userService.existsByEmail(email) || isInvited(email)) {
            throw new UserAlreadyInvitedException(String.format("User with email '%s' is already invited or created", email));
        }
        invitationRepository.save(new Invitation()
                .setEmail(email)
                .setRole(role)
                .setInvitedBy(UserAuthUtil.getCurrentUserId())
                .setInvitedDate(LocalDateTime.now(ZoneOffset.UTC)));
        log.info("Successfully invited {}-type user by email '{}'", role, email);
    }

    public Invitation getInvitation(String email) {
        return invitationRepository.findById(email)
                .orElseThrow(() -> new EntityNotFoundException("Could not find invitation for email " + email));
    }

    public Boolean isInvited(String email) {
        return invitationRepository.existsById(email);
    }
}
