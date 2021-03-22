package com.arenalocastro.videomanagement.exceptions;

public class VideoNotFoundException extends RuntimeException {

    public VideoNotFoundException(String id){
        super("Video id: " + id + " not found");
    }
}
