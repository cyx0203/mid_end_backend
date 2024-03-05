package com.gg.midend.controller;

import com.gg.core.exception.ExceptionCenter;
import com.gg.upload.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/manage/uploadFile")
    public Object uploadFile(@RequestParam("file") MultipartFile file) throws ExceptionCenter {
        return uploadService.saveFile(file);
    }

    @PostMapping("/manage/uploadMultipleFiles")
    public List<Object> uploadMultipleFiles(HttpServletRequest httpServletRequest) throws ExceptionCenter {
        return uploadService.uploadMultipleFiles(httpServletRequest);

    }

}
