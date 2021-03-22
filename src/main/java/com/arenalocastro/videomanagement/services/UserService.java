package com.arenalocastro.videomanagement.services;

import com.arenalocastro.videomanagement.exceptions.UserExistsException;
import com.arenalocastro.videomanagement.models.User;
import com.arenalocastro.videomanagement.repositories.ReactiveUserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class UserService {

    @Autowired
    ReactiveUserRepository repository;

    public Mono<User> addUser(User u){
        if(exists(u.getUsername()))
            throw new UserExistsException(u.getUsername());
        u.setRoles(Collections.singletonList("USER"));
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(u.getPassword());
        u.setPassword(hashedPassword);
        return repository.save(u);
    }

    public Mono<User> getUser(String username){
        return repository.findByUsername(username);
    }

    public Mono<User> getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();
        return getUser(principalName);
    }

    public Boolean exists(String username) {
            return repository.existsByUsername(username).block();
    }

}
