package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.service.ExcelService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/excel")
public class ExcelController {
    private final ExcelService excelService;
    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }
    @PostMapping("/test/read-excel")
    public String readExcel(@RequestParam("file") MultipartFile file) {
        try {
            excelService.readExcelSheetCategoryAccount(file);
            return "Success";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
