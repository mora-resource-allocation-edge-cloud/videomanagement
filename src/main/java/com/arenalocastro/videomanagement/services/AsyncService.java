package com.arenalocastro.videomanagement.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

    @Async
    public void run(final Runnable runnable) {
        runnable.run();
    }
}
