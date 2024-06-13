package by.baranouski.mapphotoapp.userservice.service;

import by.baranouski.mapphotoapp.userservice.exception.UserAlreadyInvitedException;
import by.baranouski.mapphotoapp.userservice.model.Invitation;
import by.baranouski.mapphotoapp.userservice.model.Role;
import by.baranouski.mapphotoapp.userservice.repository.InvitationRepository;
import by.baranouski.mapphotoapp.userservice.util.UserAuthUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {

    @Mock
    InvitationRepository mockInvitationRepo;
    @Mock
    UserService mockUserService;
    @InjectMocks
    InvitationService service;

    @Test
    void inviteUser_notExisting_created() {
        var testDate = LocalDateTime.of(2024, 1, 1, 1, 1);
        var testToken = "56687172-85f9-426e-9bd2-904c163764f4";
        var invitation = new Invitation()
                .setEmail("test@mail.org")
                .setRole(Role.ADMIN)
                .setInvitedBy(testToken)
                .setInvitedDate(testDate);

        try (var dateMock = Mockito.mockStatic(LocalDateTime.class);
             var userAuthMock = Mockito.mockStatic(UserAuthUtil.class)) {

            userAuthMock.when(UserAuthUtil::getCurrentUserId).thenReturn(testToken);
            dateMock.when(() -> LocalDateTime.now(any(ZoneOffset.class))).thenReturn(testDate);
            when(mockUserService.existsByEmail(any())).thenReturn(false);
            when(mockInvitationRepo.existsById(any())).thenReturn(false);

            service.inviteUser("test@mail.org", Role.ADMIN);

            verify(mockInvitationRepo).existsById("test@mail.org");
            verify(mockUserService).existsByEmail("test@mail.org");
            verify(mockInvitationRepo).save(invitation);
        }
    }

    @Test
    void inviteUser_isInvited_exception() {
        when(mockUserService.existsByEmail(any())).thenReturn(false);
        when(mockInvitationRepo.existsById(any())).thenReturn(true);

        assertThrows(UserAlreadyInvitedException.class,
                () -> service.inviteUser("test@mail.org", Role.ADMIN));

        verify(mockUserService).existsByEmail("test@mail.org");
        verify(mockInvitationRepo).existsById("test@mail.org");

    }

    @Test
    void inviteUser_isInService_exception() {
        when(mockUserService.existsByEmail(any())).thenReturn(true);

        assertThrows(UserAlreadyInvitedException.class,
                () -> service.inviteUser("test@mail.org", Role.ADMIN));

        verify(mockUserService).existsByEmail("test@mail.org");

    }
}
