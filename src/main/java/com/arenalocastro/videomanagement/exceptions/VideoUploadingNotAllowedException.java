package com.arenalocastro.videomanagement.exceptions;

public class VideoUploadingNotAllowedException extends RuntimeException {

    public VideoUploadingNotAllowedException() {
        super("Upload error, permission denied");
    }
}
