package by.baranouski.mapphotoapp.fileservice.controller.handler;

import _by.baranouski.mapphotoapp.api.model.ErrorDto;
import by.baranouski.mapphotoapp.fileservice.exception.EntityNotFoundException;
import by.baranouski.mapphotoapp.fileservice.exception.FileProcessingException;
import by.baranouski.mapphotoapp.fileservice.exception.MimeTypeMismatchException;
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public ErrorDto onFileProcessException(FileProcessingException ex) {
        return toBaseError(ex);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ResponseBody
    public ErrorDto onFileProcessException(MimeTypeMismatchException ex) {
        return toBaseError(ex);
    }

    private ErrorDto toBaseError(RuntimeException ex){
        return new ErrorDto().error(ex.getMessage()).timestamp(LocalDateTime.now(ZoneOffset.UTC));
    }
}
