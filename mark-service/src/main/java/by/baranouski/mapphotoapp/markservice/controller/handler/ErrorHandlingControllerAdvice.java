package by.baranouski.mapphotoapp.markservice.controller.handler;

import _by.baranouski.mapphotoapp.api.model.ErrorDto;
import by.baranouski.mapphotoapp.markservice.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDto onEntityNotFound(EntityNotFoundException ex) {
        return toBaseError(ex);
    }

    private ErrorDto toBaseError(RuntimeException ex){
        return new ErrorDto().error(ex.getMessage()).timestamp(LocalDateTime.now(ZoneOffset.UTC));
    }
}
