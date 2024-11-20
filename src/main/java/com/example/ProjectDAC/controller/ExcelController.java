package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.dto.AccountCategoryDTO;
import com.example.ProjectDAC.domain.dto.CampaignCategoryDTO;
import com.example.ProjectDAC.response.ResCategoryInExcel;
import com.example.ProjectDAC.service.ExcelService;
import com.example.ProjectDAC.util.constant.ESheetTemplateExcel;
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
            excelService.readExcelSheetCategoryAccount(file, ESheetTemplateExcel.CATEGORY_ACCOUNT.getSheetNumber());
            return "Success";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/test/write-excel-category")
    public List<ResCategoryInExcel> writeCategoryExcel() {
        return excelService.exportCategoryToSheet();
    }

//    @GetMapping("/test/write-excel")
//    public List<AccountCategoryDTO> writeExcel() {
//        return excelService.getAccountCategory();
//    }

    @GetMapping("/test/write-excel")
    public List<CampaignCategoryDTO> writeExcel() {
        return excelService.getCampaignCategoryDetails();
    }
}
