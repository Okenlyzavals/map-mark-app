package by.baranouski.mapphotoapp.userservice.service;

import by.baranouski.mapphotoapp.userservice.client.AuthProviderClient;
import by.baranouski.mapphotoapp.userservice.exception.EntityNotFoundException;
import by.baranouski.mapphotoapp.userservice.exception.UserAlreadyExistsException;
import by.baranouski.mapphotoapp.userservice.model.Invitation;
import by.baranouski.mapphotoapp.userservice.model.Role;
import by.baranouski.mapphotoapp.userservice.model.Status;
import by.baranouski.mapphotoapp.userservice.model.User;
import by.baranouski.mapphotoapp.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static by.baranouski.mapphotoapp.userservice.model.Role.ADMIN;
import static by.baranouski.mapphotoapp.userservice.model.Role.USER;
import static by.baranouski.mapphotoapp.userservice.model.Status.ACTIVE;
import static by.baranouski.mapphotoapp.userservice.model.Status.BLOCKED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    AuthProviderClient authProviderClient;
    @InjectMocks
    UserService service;

    @Test
    void createFromInvitation_uniqueEmailAndUsername_created_roleRespected(){
        var testDate = LocalDateTime.of(2024, 1, 1, 1, 1);
        var testToken = "01b0087b-54c2-4a26-9d9d-db52c0c71176";
        var password = "password";
        var user = getUser(null, null, null).setCreatedAt(null);
        var invitation = new Invitation().setRole(ADMIN);
        var expectedUser = getUser("01b0087b-54c2-4a26-9d9d-db52c0c71176", ADMIN, ACTIVE);

        try (var dateMock = Mockito.mockStatic(LocalDateTime.class)){
            dateMock.when(() -> LocalDateTime.now(any(ZoneOffset.class))).thenReturn(testDate);

            when(userRepository.existsByEmailOrUsername(any(), any())).thenReturn(Boolean.FALSE);
            when(authProviderClient.createUser(any(), any())).thenReturn(testToken);
            when(userRepository.save(any())).thenAnswer(saved -> saved.getArgument(0));

            var actual = service.createUserFromInvitation(user, invitation, password);

            assertEquals(expectedUser, actual);
        }
    }

    @Test
    void createFromInvitation_existingUsernameEmail_exception(){
        var user = getUser(null, null, null).setCreatedAt(null);
        var invitation = new Invitation().setRole(ADMIN);

        when(userRepository.existsByEmailOrUsername(any(), any())).thenReturn(Boolean.TRUE);

        assertThrows(UserAlreadyExistsException.class,
                () -> service.createUserFromInvitation(user, invitation, "pwd"));

    }

    @Test
    void createBasicUser_uniqueEmailAndUsername_created_roleUserSet(){
        var testDate = LocalDateTime.of(2024, 1, 1, 1, 1);
        var testToken = "01b0087b-54c2-4a26-9d9d-db52c0c71176";
        var password = "password";
        var user = getUser(null, null, null).setCreatedAt(null);
        var expectedUser = getUser("01b0087b-54c2-4a26-9d9d-db52c0c71176", USER, ACTIVE);

        try (var dateMock = Mockito.mockStatic(LocalDateTime.class)){
            dateMock.when(() -> LocalDateTime.now(any(ZoneOffset.class))).thenReturn(testDate);

            when(userRepository.existsByEmailOrUsername(any(), any())).thenReturn(Boolean.FALSE);
            when(authProviderClient.createUser(any(), any())).thenReturn(testToken);
            when(userRepository.save(any())).thenAnswer(saved -> saved.getArgument(0));

            var actual = service.createBasicUser(user, password);

            assertEquals(expectedUser, actual);
        }
    }

    @Test
    void createBasicUser_existingUsernameOrEmail_exception(){
        var user = getUser(null, null, null).setCreatedAt(null);

        when(userRepository.existsByEmailOrUsername(any(), any())).thenReturn(Boolean.TRUE);

        assertThrows(UserAlreadyExistsException.class,
                () -> service.createBasicUser(user, "pwd"));
    }

    @Test
    void blockUser_success(){
        var user = getUser("01b0087b-54c2-4a26-9d9d-db52c0c71176", USER, ACTIVE);
        var expectedUser = getUser("01b0087b-54c2-4a26-9d9d-db52c0c71176", USER, BLOCKED);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        service.blockUser("01b0087b-54c2-4a26-9d9d-db52c0c71176");

        verify(authProviderClient).blockUser(user);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void blockUser_notFound_exception(){
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.blockUser("01b0087b-54c2-4a26-9d9d-db52c0c71176"));

        verify(userRepository).findById("01b0087b-54c2-4a26-9d9d-db52c0c71176");
        verify(userRepository, never()).save(any());
        verify(authProviderClient, never()).blockUser(any());
    }

    @Test
    void blockUser_alreadyBlocked_skipCall(){
        var user = getUser("01b0087b-54c2-4a26-9d9d-db52c0c71176", USER, BLOCKED);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        service.blockUser(user.getId());

        verify(userRepository).findById(user.getId());

        verify(authProviderClient, never()).blockUser(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void blockUser_tryBlockAdmin_exception(){
        var user = getUser("01b0087b-54c2-4a26-9d9d-db52c0c71176", ADMIN, ACTIVE);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class,
                () -> service.blockUser("01b0087b-54c2-4a26-9d9d-db52c0c71176"));
        verify(userRepository).findById(user.getId());

        verify(authProviderClient, never()).blockUser(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void unblockUser_success(){
        var user = getUser("01b0087b-54c2-4a26-9d9d-db52c0c71176", USER, BLOCKED);
        var expectedUser = getUser("01b0087b-54c2-4a26-9d9d-db52c0c71176", USER, ACTIVE);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        service.unblockUser("01b0087b-54c2-4a26-9d9d-db52c0c71176");

        verify(authProviderClient).unblockUser(user);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void unblockUser_notFound_exception(){
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.unblockUser("01b0087b-54c2-4a26-9d9d-db52c0c71176"));

        verify(userRepository).findById("01b0087b-54c2-4a26-9d9d-db52c0c71176");
        verify(userRepository, never()).save(any());
        verify(authProviderClient, never()).unblockUser(any());
    }

    @Test
    void unblockUser_alreadyUnlocked_skipCall(){
        var user = getUser("01b0087b-54c2-4a26-9d9d-db52c0c71176", USER, ACTIVE);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        service.unblockUser(user.getId());

        verify(userRepository).findById(user.getId());

        verify(authProviderClient, never()).unblockUser(any());
        verify(userRepository, never()).save(any());
    }


    private User getUser(String id, Role role, Status status){
        return new User()
                .setId(id)
                .setUsername("YegorLetov")
                .setEmail("mail@mail.ru")
                .setRole(role)
                .setFirstName("Igor")
                .setLastName("Letov")
                .setStatus(status)
                .setCreatedAt(LocalDateTime.of(2024,1,1,1,1));
    }
}
