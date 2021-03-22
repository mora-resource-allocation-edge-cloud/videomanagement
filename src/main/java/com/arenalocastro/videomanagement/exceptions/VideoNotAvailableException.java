package com.arenalocastro.videomanagement.exceptions;

public class VideoNotAvailableException extends RuntimeException{

    public VideoNotAvailableException(String id){
        super("Video id: " + id + " has not been processed yet!");
    }
}
