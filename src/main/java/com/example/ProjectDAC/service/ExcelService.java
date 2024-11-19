package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Category;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.repository.CategoryBindingRepository;
import com.example.ProjectDAC.request.CategoryBindingRequest;
import com.example.ProjectDAC.request.CreateCategoryRequest;
import com.example.ProjectDAC.request.UpdateCategoryRequest;
import com.example.ProjectDAC.response.ResCategoryBindingDTO;
import com.example.ProjectDAC.util.constant.EKpiType;
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
    private final CategoryBindingRepository categoryBindingRepository;
    private final CategoryBindingService categoryBindingService;
    public ExcelService(CategoryService categoryService, CategoryBindingRepository categoryBindingRepository, CategoryBindingService categoryBindingService) {
        this.categoryService = categoryService;
        this.categoryBindingRepository = categoryBindingRepository;
        this.categoryBindingService = categoryBindingService;
    }
    public void readExcelSheetCategoryAccount(MultipartFile file, int sheetNumber) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            // Lấy sheet đầu tiên
//            Sheet sheet = workbook.getSheetAt(ESheetTemplateExcel.CATEGORY_ACCOUNT.getSheetNumber());
            Sheet sheet = workbook.getSheetAt(sheetNumber);
            Map<String, Integer> columnMap = new HashMap<>();
            Map<String, Integer> columnMap2 = new HashMap<>();
            List<Map<String, Object>> listActionWithCategory = new ArrayList<>();

            Map<String, Object> listActionCategoryAccount = new HashMap<>();
            boolean foundEmptyRow = false;
            Row headerRow = sheet.getRow(0);
            for(int i = 0; i <= headerRow.getPhysicalNumberOfCells(); i++) {
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
            for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows() - 1; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Map<String, Object> actionWithCategory = new HashMap<>();

                Cell categoryName = row.getCell(columnMap.get("Category Name"));
                if(categoryName != null) {
                    actionWithCategory.put("Category Name", categoryName.getStringCellValue());
                }

                Cell ankenName = row.getCell(columnMap.get("Anken Name"));
                if(ankenName != null) {
                    actionWithCategory.put("Anken Name", ankenName.getStringCellValue());
                }

                Cell action = row.getCell(columnMap.get("Action"));
                if(action != null) {
                    actionWithCategory.put("Action", action.getStringCellValue());
                }

                Cell budget = row.getCell(columnMap.get("Budget"));
                if(budget == null) {
                    actionWithCategory.put("Budget", 0);
                }
                else{
                    actionWithCategory.put("Budget", budget.getNumericCellValue());
                }

                Cell typeOfKpi = row.getCell(columnMap.get("Type of KPI"));
                if(typeOfKpi != null) {
                    actionWithCategory.put("Type of KPI", typeOfKpi.getStringCellValue());
                }

                Cell KpiGoal = row.getCell(columnMap.get("KPI Goal"));
                if(KpiGoal == null) {
                    actionWithCategory.put("KPI Goal", (long) 0);
                }
                else {
                    actionWithCategory.put("KPI Goal", KpiGoal.getNumericCellValue());
                }

                Cell startDate = row.getCell(columnMap.get("Start Date"));
                if(startDate != null) {
                    actionWithCategory.put("Start Date", startDate.getLocalDateTimeCellValue().toLocalDate());
                }

                Cell endDate = row.getCell(columnMap.get("End Date"));
                if(endDate != null) {
                    actionWithCategory.put("End Date", endDate.getLocalDateTimeCellValue().toLocalDate());
                }
                System.out.println();
                listActionWithCategory.add(actionWithCategory);
            }
            if(sheetNumber == ESheetTemplateExcel.CATEGORY_ACCOUNT.getSheetNumber()) {
                actionWithAccountCategory(sheet, columnMap2);
            }
            actionWithCategory(listActionWithCategory, sheetNumber);
        } catch (IOException | IdInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    public void actionWithCategory(List<Map<String, Object>> listActions, int sheetNumber) throws IdInvalidException {
        System.out.println("--------------Action List------------");
        System.out.println(listActions.toString());
        for(Map<String, Object> action : listActions) {
            if(Objects.equals(action.get("Action").toString(), "Update")) {
                if(!categoryService.isExistedCategoryByName(action.get("Category Name").toString())) {
                    CreateCategoryRequest request = new CreateCategoryRequest();
                    request.setName(action.get("Category Name").toString());
                    request.setBudget((Double) action.get("Budget"));
                    if(sheetNumber == ESheetTemplateExcel.CATEGORY_ACCOUNT.getSheetNumber()) {
                        request.setTypeCategory(ETypeCategory.ACCOUNT);
                    }
                    else if(sheetNumber == ESheetTemplateExcel.CATEGORY_CAMPAIGN.getSheetNumber()) {
                        request.setTypeCategory(ETypeCategory.CAMPAIGN);
                    }
                    request.setKpiType(EKpiType.valueOf(action.get("Type of KPI").toString()));
                    request.setKpiGoal((Long) action.get("KPI Goal"));
                    request.setStartDate((LocalDate) action.get("Start Date"));
                    request.setEndDate((LocalDate) action.get("End Date"));
                    request.setNameAnken(action.get("Anken Name").toString());
                    categoryService.create(request);
                    System.out.print("Create Success");
                }
                else{
                    UpdateCategoryRequest request = new UpdateCategoryRequest();
                    request.setName(action.get("Category Name").toString());
                    request.setBudget((Double) action.get("Budget"));
                    request.setStartDate((LocalDate) action.get("Start Date"));
                    request.setEndDate((LocalDate) action.get("End Date"));
                    request.setNameAnken(action.get("Anken Name").toString());
                    request.setKpiType(EKpiType.valueOf(action.get("Type of KPI").toString()));
                    request.setKpiGoal((Long) action.get("KPI Goal"));
                    Category updateCategory = categoryService.updateCategoryByName(request);
                }
            }
            else if(Objects.equals(action.get("Action").toString(),"Delete")) {
                categoryService.deleteCategoryByName(action.get("Category Name").toString());
                System.out.print("Delete Success");
            }
        }
    }

    public void actionWithAccountCategory(Sheet sheet, Map<String, Integer> columnMap2) throws IdInvalidException {
        System.out.println("--------------Action List------------");
        List<Map<String, Object>> listActions = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            Map<String, Object> actionWithAccountCategory = new HashMap<>();
            // Table 2
            Cell accountId = row.getCell(columnMap2.get("Account Id"));
            if(accountId != null) {
                actionWithAccountCategory.put("Account Id", accountId.getNumericCellValue());
            }

            Cell categoryId = row.getCell(columnMap2.get("Category Id"));
            if(categoryId != null) {
                actionWithAccountCategory.put("Category Id", categoryId.getNumericCellValue());
            }

            Cell actionAccountCategory = row.getCell(columnMap2.get("Action"));
            if(actionAccountCategory != null) {
                actionWithAccountCategory.put("Action", actionAccountCategory.getStringCellValue());
            }
            System.out.println();
            listActions.add(actionWithAccountCategory);
        }

        // Giả sử bạn lấy được giá trị từ listActions là Double
        for(Map<String, Object> action : listActions) {
            if(action.get("Action") == null) {
                continue;
            }
            Object categoryIdObj = action.get("Category Id");
            Object accountIdObj = action.get("Account Id");

            if(Objects.equals(action.get("Action").toString(), "Update")) {
                CategoryBindingRequest request = new CategoryBindingRequest();
                request.setCategoryId((((Double) categoryIdObj).longValue()));
                request.setEntityId((((Double) accountIdObj).longValue()));
                request.setEntityType(ETypeCategory.ACCOUNT);
                if(!this.categoryBindingRepository.existsByEntityIdAndEntityType(request.getEntityId(), ETypeCategory.ACCOUNT)) {
                    this.categoryBindingService.create(request);
                    System.out.print("Create Account-Category Success");
                }
                else {
                    this.categoryBindingService.update(request);
                    System.out.print("Update Account-Category Success");
                }
            }
            else if(Objects.equals(action.get("Action").toString(), "Delete")) {
                this.categoryBindingService.deleteCategoryAccount((((Double) accountIdObj).longValue()), ETypeCategory.ACCOUNT);
                System.out.print("Delete Account-Category Success");
            }
        }
    }
}
