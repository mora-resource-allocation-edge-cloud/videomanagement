package com.arenalocastro.videomanagement.repositories;

import com.arenalocastro.videomanagement.models.Video;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactiveVideoRepository extends ReactiveCrudRepository<Video, ObjectId> {
}
