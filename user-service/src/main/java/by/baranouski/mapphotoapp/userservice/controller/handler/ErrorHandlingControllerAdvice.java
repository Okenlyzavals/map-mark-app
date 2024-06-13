package by.baranouski.mapphotoapp.userservice.controller.handler;

import _by.baranouski.mapphotoapp.api.model.ErrorDto;
import by.baranouski.mapphotoapp.userservice.exception.EntityNotFoundException;
import by.baranouski.mapphotoapp.userservice.exception.KeycloakException;
import by.baranouski.mapphotoapp.userservice.exception.UserAlreadyExistsException;
import by.baranouski.mapphotoapp.userservice.exception.UserAlreadyInvitedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@ControllerAdvice
public class ErrorHandlingControllerAdvice {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDto onEntityNotFound(EntityNotFoundException ex) {
        return toBaseError(ex);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public ErrorDto onKeycloakException(KeycloakException ex) {
        return toBaseError(ex);
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            UserAlreadyInvitedException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDto onFileProcessException(UserAlreadyExistsException ex) {
        return toBaseError(ex);
    }

    private ErrorDto toBaseError(RuntimeException ex) {
        return new ErrorDto().error(ex.getMessage()).timestamp(LocalDateTime.now(ZoneOffset.UTC));
    }
}
