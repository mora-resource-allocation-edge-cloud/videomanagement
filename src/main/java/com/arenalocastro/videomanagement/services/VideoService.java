package com.arenalocastro.videomanagement.services;

import com.arenalocastro.videomanagement.exceptions.*;
import com.arenalocastro.videomanagement.models.User;
import com.arenalocastro.videomanagement.models.Video;
import com.arenalocastro.videomanagement.models.VideoStatus;
import com.arenalocastro.videomanagement.repositories.ReactiveVideoRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class VideoService {

    @Autowired
    ReactiveVideoRepository repository;
    @Autowired
    FileService fileService;
    @Autowired
    RequestService requestService;
    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;
    @Value(value="${KAFKA_MAIN_TOPIC}")
    private String maintopic;
    @Value(value = "${EXPOSED_URL}")
    private String externalURL;

    public Flux<Video> getVideos() {
        return repository.findAll().filter((v) ->
            v.getStatus() == VideoStatus.Available || v.getStatus() == VideoStatus.AvailableWithOnlineEncoding
        );
    }

    public Mono<Video> saveVideo(Video video){
        return repository.save(video);
    }

    public Mono<Video> getVideo(String id){
        return repository.findById(new ObjectId(id));
    }

    public boolean exists(String id) {
        try {
            return repository.existsById(new ObjectId(id)).block();
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean belongsToUser(Video video, User user){
        return video.getUser().toHexString().equals(user.get_id_string());
    }

    public Mono<Video> uploadVideo(String id, MultipartFile file, User user){
        Video video = getVideo(id).block();
        if(video == null)
            throw new VideoNotFoundException(id);
        if(!isWaitUpload(video))
            throw new VideoAlreadyUploaded();
        if(!belongsToUser(video, user))
            throw new VideoUploadingNotAllowedException();
        Boolean res = fileService.uploadFile(file,id);
        if(!res)
            throw new InternalException("Error in uploading file");
        video.setStatus(VideoStatus.Uploaded);
        return repository.save(video).flatMap(v -> {
            if (kafkaTemplate != null)
                kafkaTemplate.send(maintopic, "process|" + video.get_id_string());
            return Mono.just(video);
        });
    }


    public Boolean isWaitUpload(Video video) {
        return video.getStatus().equals(VideoStatus.WaitingUpload);
    }

    public Boolean isAvailable(Video video){
        return video.getStatus().equals(VideoStatus.Available);
    }

    public ResponseEntity<String> getUploadedVideo(String id){
        Video video =   getVideo(id).block(); // 1
        /*Flux<Video> videos = getVideos(); // 2
        int i = 0;
        for (Video v : videos.toIterable()) {
            getVideo(v.get_id_string()).block();
            //System.out.println(v.get_id_string());
            i++;
            if (i > 10)
                break;
        }*/
        if(video == null)
            throw new VideoNotFoundException(id);
        if(!isAvailable(video))
            throw new VideoNotAvailableException(id);
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            httpHeaders.setLocation(new URI(externalURL + "/videofiles/" + id + "/video.mpd"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new InternalException("Error in URI syntax");
        }
        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
    }

}

