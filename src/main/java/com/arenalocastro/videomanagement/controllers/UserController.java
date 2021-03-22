package com.arenalocastro.videomanagement.controllers;

import com.arenalocastro.videomanagement.models.User;
import com.arenalocastro.videomanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class UserController {

    @Autowired
    UserService userservice;

    @PostMapping(path = "/register")
    public ResponseEntity<Mono<User>> register(@RequestBody User u) {
        return new ResponseEntity<>(userservice.addUser(u), HttpStatus.CREATED);
    }
}
