package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.FileUpload;
import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.service.FileUploadService;
import com.example.ProjectDAC.service.UserService;
import com.example.ProjectDAC.util.FileDownloadUtil;
import com.example.ProjectDAC.util.FileUploadUtil;
import com.nimbusds.jose.util.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
public class FileController {
    private final FileUploadService fileUploadService;
    private final UserService userService;
    public FileController(FileUploadService fileUploadService, UserService userService) {
        this.fileUploadService = fileUploadService;
        this.userService = userService;
    }

//    @PostMapping("/uploadFile")
//    public String uploadFile(
//            @RequestParam("file") MultipartFile multipartFile)
//            throws IOException {
//        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
//        long size = multipartFile.getSize();
//
//        String filecode = FileUploadUtil.saveFile(fileName, multipartFile);
//
////        FileUploadResponse response = new FileUploadResponse();
////        response.setFileName(fileName);
////        response.setSize(size);
////        response.setDownloadUri("/downloadFile/" + filecode);
//
//        return "Upload Success";
//    }

    @GetMapping("/downloadFile/{fileCode}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileCode") String fileCode) {
        FileDownloadUtil downloadUtil = new FileDownloadUtil();

        UrlResource resource = null;
        try {
            resource = downloadUtil.getFileAsResource(fileCode);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

    @GetMapping("/fileUpload")
    public ResponseEntity<List<FileUpload>> getFileUpload() {
        User user = this.userService.getUserFromSecurityContext();
        List<FileUpload> res =  user.getFileUploads();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
