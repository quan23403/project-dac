package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.FileUpload;
import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.request.RequestBodyExport;
import com.example.ProjectDAC.response.ResCategoryInExcel;
import com.example.ProjectDAC.service.ExcelService;
import com.example.ProjectDAC.service.FileUploadService;
import com.example.ProjectDAC.service.UserService;
import com.example.ProjectDAC.util.FileUploadUtil;
import com.example.ProjectDAC.util.constant.EStatusFile;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/excel")
public class ExcelController {
    private final ExcelService excelService;
    private final FileUploadService fileUploadService;
    private final UserService userService;
    public ExcelController(ExcelService excelService, UserService userService, FileUploadService fileUploadService) {
        this.excelService = excelService;
        this.userService = userService;
        this.fileUploadService = fileUploadService;
    }
    @PostMapping("/read-excel")
    public String readExcel(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        long size = file.getSize();
        String filecode = FileUploadUtil.saveFile(fileName, file);
        String downloadUri = "/downloadFile/" + filecode;
        User user = this.userService.getUserFromSecurityContext();
        try {
            List<Long> ids = this.userService.getAnkenListFromSecurityContext();
            excelService.readExcelSheet(file, ids);
            FileUpload fileUpload = new FileUpload(user, EStatusFile.SUCCESS, downloadUri, size,  fileName, LocalDateTime.now());
            this.fileUploadService.create(fileUpload);
            return "Success";
        } catch (Exception e) {
            FileUpload fileUpload = new FileUpload(user, EStatusFile.FAIL, downloadUri, size,  fileName, LocalDateTime.now());
            this.fileUploadService.create(fileUpload);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/write-excel-category")
    public List<ResCategoryInExcel> writeCategoryExcel() throws IdInvalidException {
        List<Long> ids = this.userService.getAnkenListFromSecurityContext();
        return excelService.getCategory(ETypeCategory.CAMPAIGN, ids);
    }

    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) {
        List<Long> ids = this.userService.getAnkenListFromSecurityContext();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader("Content-Disposition", headerValue);
        this.excelService.exportData(response, ids);
    }

    @PostMapping("/export")
    public void exportExcelTest(HttpServletResponse response, @RequestBody RequestBodyExport requestBodyExport) {
        List<Long> ids = requestBodyExport.getIds();
        List<Long> ankenIdList = this.userService.getAnkenListFromSecurityContext();

        ids.removeIf(id -> !ankenIdList.contains(id));

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader("Content-Disposition", headerValue);
        this.excelService.exportData(response, ids);
    }

    @PostMapping("/preview")
    public ResponseEntity<List<Map<String, Object>>> getCategoryPreview(@RequestParam("file") MultipartFile file) {
        try{
            Map<String, Object> categoryActions = new HashMap<>();
            categoryActions.put("name", "Category");
            categoryActions.put("data", this.excelService.getCategoryActionPreview(file));

            Map<String, Object> categoryAccountActions = new HashMap<>();
            categoryAccountActions.put("name", "Category-Account");
            categoryAccountActions.put("data", this.excelService.getCategoryAccountActionPreview(file));

            Map<String, Object> categoryCampaignActions = new HashMap<>();
            categoryCampaignActions.put("name", "Category-Campaign");
            categoryCampaignActions.put("data", this.excelService.getCategoryCampaignActionPreview(file));

            List<Map<String, Object>> response = new ArrayList<>();
            response.add(categoryActions);
            response.add(categoryAccountActions);
            response.add(categoryCampaignActions);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
