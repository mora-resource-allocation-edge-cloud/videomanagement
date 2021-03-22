package com.arenalocastro.videomanagement.exceptions;

public class VideoAlreadyUploaded extends RuntimeException {
    public VideoAlreadyUploaded(){
        super("Error, video already uploaded");
    }
}
