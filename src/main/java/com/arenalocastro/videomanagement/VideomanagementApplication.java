package com.arenalocastro.videomanagement;

import com.mongodb.MongoCommandException;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class VideomanagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideomanagementApplication.class, args);
    }


}
