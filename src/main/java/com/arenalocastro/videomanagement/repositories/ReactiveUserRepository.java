package com.arenalocastro.videomanagement.repositories;

import com.arenalocastro.videomanagement.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ReactiveUserRepository extends ReactiveCrudRepository<User, ObjectId> {
    Mono<User> findByUsername(String username);
    Mono<Boolean> existsByUsername(String username);
}
