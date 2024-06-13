package by.baranouski.mapphotoapp.userservice.exception;

public class UserAlreadyInvitedException extends RuntimeException {
    public UserAlreadyInvitedException(String message) {
        super(message);
    }
}
