package me.loki2302;

import org.springframework.web.multipart.MultipartFile;

public class CustomFile {
    private MultipartFile file;
    
    public void setFile(MultipartFile file) {
        this.file = file;
    }
    
    public MultipartFile getFile() {
        return file;
    }
}