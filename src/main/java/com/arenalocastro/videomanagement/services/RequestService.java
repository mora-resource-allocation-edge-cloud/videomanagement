package com.arenalocastro.videomanagement.services;

import com.arenalocastro.videomanagement.models.Post;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Service
public class RequestService {
    private RestTemplate restTemplate = new RestTemplate();

    public boolean processRequest(String id) {
        String url = "http://videoserver-videoprocessing:5000/videos/process";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, String> map = new HashMap<>();
        map.put("videoId", id);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<Post> response = this.restTemplate.postForEntity(url, entity, Post.class);
        if (response.getStatusCode() != HttpStatus.OK)
            return false;
        return true;
    }

}
