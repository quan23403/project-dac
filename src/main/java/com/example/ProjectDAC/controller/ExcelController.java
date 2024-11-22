package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.response.ResCategoryInExcel;
import com.example.ProjectDAC.service.ExcelService;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
            excelService.readExcelSheetCategory(file);
            return "Success";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/test/write-excel-category")
    public List<ResCategoryInExcel> writeCategoryExcel() {
        return excelService.getCategory(ETypeCategory.CAMPAIGN);
    }

    @GetMapping("/export")
    public String exportExcel() {
        this.excelService.exportData();
        return "Export Successfully";
    }
//    @GetMapping("/test/write-excel")
//    public List<AccountCategoryDTO> writeExcel() {
//        return excelService.getAccountCategory();
//    }

//    @GetMapping("/test/write-excel")
//    public void writeExcel() {
//        this.excelService.writeSheetAccountCategory();
//    }
}
