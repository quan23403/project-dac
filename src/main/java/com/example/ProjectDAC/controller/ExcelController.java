package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.response.ResCategoryInExcel;
import com.example.ProjectDAC.service.ExcelService;
import com.example.ProjectDAC.service.UserService;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/excel")
public class ExcelController {
    private final ExcelService excelService;
    private final UserService userService;
    public ExcelController(ExcelService excelService, UserService userService) {
        this.excelService = excelService;
        this.userService = userService;
    }
    @PostMapping("/read-excel")
    public String readExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<Long> ids = this.userService.getAnkenListFromSecurityContext();
            excelService.readExcelSheet(file, ids);
            return "Success";
        } catch (Exception e) {
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
