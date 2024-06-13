package by.baranouski.mapphotoapp.userservice.controller;

import _by.baranouski.mapphotoapp.api.model.InvitationRequestDto;
import _by.baranouski.mapphotoapp.api.model.RegistrationRequestDto;
import _by.baranouski.mapphotoapp.api.model.UserDto;
import by.baranouski.mapphotoapp.api.UsersApi;
import by.baranouski.mapphotoapp.userservice.mapper.UserMapper;
import by.baranouski.mapphotoapp.userservice.service.InvitationService;
import by.baranouski.mapphotoapp.userservice.service.UserService;
import by.baranouski.mapphotoapp.userservice.util.LinkAssembler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController implements UsersApi {
    UserService userService;
    InvitationService invitationService;
    UserMapper userMapper;
    LinkAssembler linkAssembler;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> inviteUser(InvitationRequestDto invitationRequestDto) {
        invitationService.inviteUser(
                invitationRequestDto.getEmail(),
                userMapper.toRole(invitationRequestDto.getRole()));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> createUser(RegistrationRequestDto registrationRequestDto) {
        var user = userMapper.toUser(registrationRequestDto);
        var isInvited = invitationService.isInvited(user.getEmail());
        var res = Boolean.TRUE.equals(isInvited)
                ? userService.createUserFromInvitation(user, invitationService.getInvitation(user.getEmail()), registrationRequestDto.getPassword())
                : userService.createBasicUser(user, registrationRequestDto.getPassword());

        var link = linkAssembler.getResponseEntity(res);

        return ResponseEntity.created(link.toUri()).build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> blockUser(String id) {
        userService.blockUser(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userAuthUtil.isUserAccessible(#id))")
    public ResponseEntity<UserDto> getUser(String id) {
        return ResponseEntity.ok(
                userMapper.toDto(
                        userService.findUser(id)));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unblockUser(String id) {
        userService.unblockUser(id);
        return ResponseEntity.ok().build();
    }
}
