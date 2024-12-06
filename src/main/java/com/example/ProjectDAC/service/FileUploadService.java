package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.FileUpload;
import com.example.ProjectDAC.repository.FileUploadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileUploadService {
    private final FileUploadRepository fileUploadRepository;
    public FileUploadService(FileUploadRepository fileUploadRepository) {
        this.fileUploadRepository = fileUploadRepository;
    }
    public void create(FileUpload fileUpload) {
        this.fileUploadRepository.save(fileUpload);
    }

    public List<FileUpload> getFileUpload(long userId) {
        return this.fileUploadRepository.findByUserId(userId);
    }
}
