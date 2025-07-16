package com.moneydiary.backend.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${file.upload-path}")
    private String fileDir;

    public String storeFile(MultipartFile multipartFile){
        if(multipartFile.isEmpty()){
            //파일이 없으면 예외? 아니면 그냥 무시?
        }

        String originalFilename= multipartFile.getOriginalFilename();
        String ext = extracted(originalFilename);
        String uuid = UUID.randomUUID().toString();
        String storeFilename = uuid + "." + ext;
        try{
            multipartFile.transferTo(new File(getFullPath(storeFilename)));
        }catch(IOException e){
            //예외변환
        }
        return storeFilename;
    }

    private String getFullPath(String storeFilename) {
        return fileDir + storeFilename;
    }

    private String extracted(String originalFilename) {
        int index = originalFilename.lastIndexOf(".");
        return originalFilename.substring(index + 1);
    }

}
