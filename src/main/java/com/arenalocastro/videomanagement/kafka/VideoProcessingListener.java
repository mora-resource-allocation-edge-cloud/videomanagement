package com.arenalocastro.videomanagement.kafka;

import com.arenalocastro.videomanagement.exceptions.VideoNotFoundException;
import com.arenalocastro.videomanagement.models.Video;
import com.arenalocastro.videomanagement.models.VideoStatus;
import com.arenalocastro.videomanagement.repositories.ReactiveVideoRepository;
import com.arenalocastro.videomanagement.services.VideoService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;

@Service
@ConditionalOnProperty(
        value="deployment.needKafka",
        havingValue="true",
        matchIfMissing=true
)
public class VideoProcessingListener {
    @Autowired
    VideoService videoService;

    @Autowired
    ReactiveVideoRepository repository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${KAFKA_MAIN_TOPIC}")
    private String mainTopic;

    @KafkaListener(topics="${KAFKA_MAIN_TOPIC}")
    public void listen(String message) {
        System.out.println("Received message " + message);
        String[] messageParts = message.split("\\|");

        if (messageParts[0].equals("processed")) {
            String videoId = messageParts[1];
            setVideoStatus(videoId, VideoStatus.Available);
        }

        if (messageParts[0].equals("processingFailed")) {
            String videoId = messageParts[1];
            setVideoStatus(videoId, VideoStatus.NotAvailable);
            File videomp4 = new File("/video/"+videoId+"/video.mp4");
            videomp4.delete();
            File dir = new File("/video/"+videoId);
            dir.delete();
        }
    }
    private void setVideoStatus(String id, VideoStatus status){
        Video video = videoService.getVideo(id).block();
        if(video != null){
            video.setStatus(status);
            videoService.saveVideo(video).block();
        }
    }
}
