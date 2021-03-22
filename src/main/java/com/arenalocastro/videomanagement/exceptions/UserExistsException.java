package com.arenalocastro.videomanagement.exceptions;

public class UserExistsException extends RuntimeException {

    public UserExistsException(String username){
        super("user: " + username + " already exists");
    }
}
