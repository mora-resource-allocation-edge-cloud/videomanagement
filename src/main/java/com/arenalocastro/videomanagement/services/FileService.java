package com.arenalocastro.videomanagement.services;

import com.arenalocastro.videomanagement.exceptions.FileEmptyException;
import com.arenalocastro.videomanagement.exceptions.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

@Service
public class FileService {
    @Autowired
    ServletContext context;

    public boolean uploadFile(MultipartFile file, String id){
        if(!file.isEmpty()){
            String realPath = "/video/"+id;
            File f = new File(realPath);
            if(! f.exists())
                f.mkdirs();
            realPath += "/video.mp4";
            File dest = new File(realPath);
            try {
                file.transferTo(dest);
            } catch (IOException e) {
                e.printStackTrace();
                throw new InternalException("Error in file transfer");
            }
            return true;
        }
        throw new FileEmptyException();
    }
}
