package com.arenalocastro.videomanagement.exceptions;

public class FileEmptyException extends RuntimeException {

    public FileEmptyException(){
        super("Update error, empty file");
    }
}
