package com.arenalocastro.videomanagement.controllers;

import com.arenalocastro.videomanagement.exceptions.VideoNotFoundException;
import com.arenalocastro.videomanagement.models.User;
import com.arenalocastro.videomanagement.models.Video;
import com.arenalocastro.videomanagement.models.VideoStatus;
import com.arenalocastro.videomanagement.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class VideoController {
    @Autowired
    RequestService requestService;
    @Autowired
    FileService fileService;
    @Autowired
    VideoService videoService;
    @Autowired
    UserService userService;

    @Autowired
    CloudEdgeService cloudEdgeService;

    @GetMapping(path="/videos")
    public Flux<Video> getVideos(){
        return videoService.getVideos();
    }

    @PostMapping(path="/videos", consumes = "application/json")
    public Mono<Video> newVideo(@RequestBody Video v){
        User u = userService.getAuthenticatedUser().block();
        v.setUser(u.get_id());
        v.setStatus(VideoStatus.WaitingUpload);
        return videoService.saveVideo(v);
    }

    @PostMapping(path = "/videos/{id}")
    public  @ResponseBody
    ResponseEntity<Video> uploadVideo(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        User user = userService.getAuthenticatedUser().block();
        Video video = videoService.uploadVideo(id, file, user).block();
        return new ResponseEntity<>(video, HttpStatus.OK);
    }

    @GetMapping(path = "/videos/{id}")
    public ResponseEntity<String> getVideo(@PathVariable String id){
        try {
            System.out.println("Retrieving video " + id);
            ResponseEntity<String> uri = videoService.getUploadedVideo(id);
            if (cloudEdgeService.isCloudAndOnlineEncoding()) {
                Video v = videoService.getVideo(id).block();
                if (v != null &&
                    v.getStatus() == VideoStatus.AvailableWithOnlineEncoding)
                    cloudEdgeService.onlineEncodeVideo(id);
                // TODO concurrency
            }
            return uri;
        } catch(VideoNotFoundException e) {
            System.out.println("Video not found exception");
            return cloudEdgeService.handleNotFoundVideo(id);
        }
    }
}
