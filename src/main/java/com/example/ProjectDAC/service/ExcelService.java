package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Category;
import com.example.ProjectDAC.util.constant.ESheetTemplateExcel;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class ExcelService {
    private final CategoryService categoryService;
    public ExcelService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    public void readExcelSheetCategoryAccount(MultipartFile file) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            // Lấy sheet đầu tiên
            Sheet sheet = workbook.getSheetAt(ESheetTemplateExcel.CATEGORY_ACCOUNT.getSheetNumber());
            Map<String, Integer> columnMap = new HashMap<>();
            Map<String, Integer> columnMap2 = new HashMap<>();
            Map<String, Object> listActionWithCategory = new HashMap<>();
            boolean foundEmptyRow = false;
            Row headerRow = sheet.getRow(0);
            for(int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                Cell headerCell = headerRow.getCell(i);
                if (headerCell == null || headerCell.getCellType() == CellType.BLANK) {
                    foundEmptyRow = true;
                    continue;
                }
                if(!foundEmptyRow) {
                    columnMap.put(headerCell.getStringCellValue(), headerCell.getColumnIndex());
                }
                else {
                    columnMap2.put(headerCell.getStringCellValue(), headerCell.getColumnIndex());
                }
            }
            for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Cell categoryName = row.getCell(columnMap.get("Category Name"));
                if(categoryName != null) {
                    listActionWithCategory.put("Category Name", categoryName.getStringCellValue());
                }

                Cell action = row.getCell(columnMap.get("Action"));
                if(action != null) {
                    listActionWithCategory.put("Action", action.getStringCellValue());
                }

                Cell budget = row.getCell(columnMap.get("Budget"));
                if(budget != null) {
                    listActionWithCategory.put("Budget", budget.getNumericCellValue());
                }

                Cell typeOfKpi = row.getCell(columnMap.get("Type of KPI"));
                if(typeOfKpi != null) {
                    listActionWithCategory.put("Type of KPI", typeOfKpi.getStringCellValue());
                }

                Cell startDate = row.getCell(columnMap.get("Start Date"));
                if(startDate != null) {
                    listActionWithCategory.put("Start Date", startDate.getLocalDateTimeCellValue().toLocalDate());
                }

                Cell endDate = row.getCell(columnMap.get("End Date"));
                if(endDate != null) {
                    listActionWithCategory.put("End Date", endDate.getLocalDateTimeCellValue().toLocalDate());
                }
                System.out.println();
                actionWithCategory(listActionWithCategory);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void actionWithCategory(Map<String, Object> listActions) {
        System.out.println("--------------Action List------------");
        System.out.println(listActions.toString());
        if(Objects.equals(listActions.get("Action").toString(), "Update")) {
            Category category = new Category();
            category.setName(listActions.get("Category Name").toString());
            category.setBudget((Double) listActions.get("Budget"));
            category.setTypeCategory(ETypeCategory.ACCOUNT);
            category.setStartDate((LocalDate) listActions.get("Start Date"));
            category.setEndDate((LocalDate) listActions.get("End Date"));
            if(!categoryService.isExistCategory(category.getName())) {
                categoryService.create(category);
                System.out.print("Create Success");
            }
            else{
                Category updateCategory = categoryService.updateCategoryByName(category);
            }
        }
        else if(Objects.equals(listActions.get("Action").toString(), "Delete")) {
            categoryService.deleteCategoryByName(listActions.get("Category Name").toString());
            System.out.print("Delete Success");
        }
    }
}
