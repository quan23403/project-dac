package com.example.ProjectDAC.domain;

import com.example.ProjectDAC.controller.FileController;
import com.example.ProjectDAC.util.constant.EStatusFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_upload")
@Getter
@Setter
public class FileUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String fileName;
    private long size;
    private String downLoadUri;

    @Enumerated(EnumType.STRING)
    private EStatusFile status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private LocalDateTime uploadDate;

    public FileUpload(User user, EStatusFile status, String downLoadUri, long size, String fileName, LocalDateTime uploadDate) {
        this.user = user;
        this.status = status;
        this.downLoadUri = downLoadUri;
        this.size = size;
        this.fileName = fileName;
        this.uploadDate =  uploadDate;
    }

    public FileUpload() {

    }
}
