package com.arenalocastro.videomanagement.controllers;

import com.arenalocastro.videomanagement.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionsHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String genericExceptionHandler(Exception exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(VideoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String videoNotFoundHandler(VideoNotFoundException exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(VideoNotAvailableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String videoNotAvailableHandler(VideoNotAvailableException exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String userExistsHandler(UserExistsException exception) {
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(VideoUploadingNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String videoUploadingNotAllowedHandler(VideoUploadingNotAllowedException exception){
        return exception.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(InternalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String InternalExceptionHandler(InternalException exception){
        return exception.getMessage();
    }


    @ResponseBody
    @ExceptionHandler(FileEmptyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String FileEmptyHandler(FileEmptyException exception){
        return exception.getMessage();
    }


    @ResponseBody
    @ExceptionHandler(VideoAlreadyUploaded.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String VideoAlredyUploadedHandler(VideoAlreadyUploaded exception){
        return exception.getMessage();
    }

}
