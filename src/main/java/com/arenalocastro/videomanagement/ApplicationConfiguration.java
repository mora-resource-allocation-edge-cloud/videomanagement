package com.arenalocastro.videomanagement;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.reactivestreams.client.*;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableReactiveMongoRepositories
@SpringBootApplication(exclude = {MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class})
@AutoConfigureAfter(EmbeddedMongoAutoConfiguration.class)
@EnableAsync
public class ApplicationConfiguration extends AbstractReactiveMongoConfiguration {
    @Value(value = "${MONGO_HOST}")
    private String mongoHost;

    @Value(value = "${MONGO_ROOT_USERNAME}")
    private String mongoUser;

    @Value(value = "${MONGO_ROOT_PASSWORD}")
    private String mongoPass;

    @Value(value = "${MONGO_PORT:27017}")
    private String mongoPort;

    @Value(value = "${MONGO_DBNAME}")
    private String mongoDatabase;

    @Value(value = "${MONGO_DBNAME_AUTH}")
    private String mongoDatabaseAuth;


    @Value("#{new Boolean('${IS_CLOUD:true}')}")
    private Boolean isCloud;

    @Value("#{new Integer('${MAX_VIDEO:10}')}")
    private Integer maxDocuments;

    public ApplicationConfiguration() {
    }

    @Bean
    AsyncTaskExecutor taskExecutor () {
        SimpleAsyncTaskExecutor t = new SimpleAsyncTaskExecutor();
        t.setConcurrencyLimit(100); // TODO set concurrency limit
        return t;
    }

    @Override
    @Bean
    public MongoClient reactiveMongoClient() {
        String s = String.format("mongodb://%s:%s@%s:%s/%s",
                mongoUser, mongoPass, mongoHost, mongoPort, mongoDatabase);
        MongoClient cl = MongoClients.create(s);
        return cl;
    }

    @Override
    protected String getDatabaseName() {
        return mongoDatabase;
    }

    @EventListener
    public void setCollection(final ApplicationReadyEvent event) {
        System.out.println("SET COLLECTION!!");
        if (!this.isCloud) {
            MongoDatabase db = reactiveMongoClient().getDatabase(mongoDatabase);
            System.out.println("creating collection...");
            CreateCollectionOptions options = new CreateCollectionOptions().capped(true).sizeInBytes(0x10000000).maxDocuments(maxDocuments);
            try {
                /*db.createCollection("video", options).subscribe(new Subscriber<Success>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        System.out.println(subscription.toString());
                    }

                    @Override
                    public void onNext(Success success) {
                        System.out.println(success.toString());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println(throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Complete");
                    }
                });*/
            } catch (MongoCommandException e) {
                // Collection exists? TODO be more specific
                System.out.println("Collection maybe exists... we should check if it is already capped");
            }
        }
    }
}
