package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Anken;
import com.example.ProjectDAC.domain.Category;
import com.example.ProjectDAC.domain.dto.AccountCategoryDTO;
import com.example.ProjectDAC.domain.dto.CampaignCategoryDTO;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.repository.CategoryBindingRepository;
import com.example.ProjectDAC.request.CategoryBindingRequest;
import com.example.ProjectDAC.request.CreateCategoryRequest;
import com.example.ProjectDAC.request.UpdateCategoryRequest;
import com.example.ProjectDAC.response.ResCategoryInExcel;
import com.example.ProjectDAC.util.constant.EKpiType;
import com.example.ProjectDAC.util.constant.ESheetTemplateExcel;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ExcelService {
    private final CategoryService categoryService;
    private final CategoryBindingRepository categoryBindingRepository;
    private final CategoryBindingService categoryBindingService;
    private final AnkenService ankenService;
    private String[] actionsValues =  {"Update", "Delete", "None"};
    public ExcelService(CategoryService categoryService, CategoryBindingRepository categoryBindingRepository,
                        AnkenService ankenService, CategoryBindingService categoryBindingService) {
        this.categoryService = categoryService;
        this.categoryBindingRepository = categoryBindingRepository;
        this.categoryBindingService = categoryBindingService;
        this.ankenService = ankenService;
    }

    public void readExcelSheetCategory(MultipartFile file) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            // Lấy sheet đầu tiên
//            Sheet sheet = workbook.getSheetAt(ESheetTemplateExcel.CATEGORY_ACCOUNT.getSheetNumber());
            for(int sheetNumber = ESheetTemplateExcel.CATEGORY_ACCOUNT.getSheetNumber(); sheetNumber <= ESheetTemplateExcel.CATEGORY_CAMPAIGN.getSheetNumber(); sheetNumber++) {
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
                for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if(row == null) {
                        break;
                    }
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
                actionWithCategory(listActionWithCategory, sheetNumber);
                if(sheetNumber == ESheetTemplateExcel.CATEGORY_ACCOUNT.getSheetNumber()) {
                    actionWithAccountCategory(sheet, columnMap2);
                } else if (sheetNumber == ESheetTemplateExcel.CATEGORY_CAMPAIGN.getSheetNumber()) {
                    actionWithCampaignCategory(sheet, columnMap2);
                }
            }
        } catch (IOException | IdInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    public void actionWithCategory(List<Map<String, Object>> listActions, int sheetNumber) throws IdInvalidException {
//        System.out.println("--------------Action List------------");
//        System.out.println(listActions.toString());
        for(Map<String, Object> action : listActions) {
            if(action.get("Action") == null) {
                break;
            }
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
                    request.setKpiGoal(((Double) action.get("KPI Goal")).longValue());
                    request.setStartDate((LocalDate) action.get("Start Date"));
                    request.setEndDate((LocalDate) action.get("End Date"));
                    request.setNameAnken(action.get("Anken Name").toString());
                    categoryService.create(request);
                    System.out.print("Create Category Success");
                }
                else{
                    UpdateCategoryRequest request = new UpdateCategoryRequest();
                    request.setName(action.get("Category Name").toString());
                    request.setBudget((Double) action.get("Budget"));
                    request.setStartDate((LocalDate) action.get("Start Date"));
                    request.setEndDate((LocalDate) action.get("End Date"));
                    request.setNameAnken(action.get("Anken Name").toString());
                    request.setKpiType(EKpiType.valueOf(action.get("Type of KPI").toString()));
                    request.setKpiGoal(((Double) action.get("KPI Goal")).longValue());
                    categoryService.updateCategoryByName(request);
                    System.out.print("Update Category Success");
                }
            }
            else if(Objects.equals(action.get("Action").toString(),"Delete")) {
                categoryService.deleteCategoryByName(action.get("Category Name").toString());
                System.out.print("Delete Category Success");
            }
        }
    }

    public void actionWithCampaignCategory(Sheet sheet, Map<String, Integer> columnMap2) throws IdInvalidException {
        List<Map<String, Object>> listActions = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if(row == null || row.getCell(columnMap2.get("Action")) == null) {
                break;
            }
            Map<String, Object> action = new HashMap<>();

            Cell campaignId = row.getCell(columnMap2.get("Campaign Id"));
            if (campaignId != null) {
                action.put("Campaign Id", campaignId.getNumericCellValue());
            }

            Cell categoryId = row.getCell(columnMap2.get("Category Id"));
            if (categoryId != null) {
                action.put("Category Id", categoryId.getNumericCellValue());
            }

            Cell categoryName = row.getCell(columnMap2.get("Category Name"));
            if (categoryName != null) {
                action.put("Category Name", categoryName.getStringCellValue());
            }

            Cell actionAccountCategory = row.getCell(columnMap2.get("Action"));
            if (actionAccountCategory != null) {
                action.put("Action", actionAccountCategory.getStringCellValue());
            }
            listActions.add(action);
        }
        // Giả sử bạn lấy được giá trị từ listActions là Double
        for (Map<String, Object> action : listActions) {
            if (action.get("Action") == "None") {
                continue;
            }
            if (Objects.equals(action.get("Action").toString(), "Update")) {
                if (action.get("Category Id") == null) {
                    Category category = this.categoryService.getCategoryByName((String) action.get("Category Name"));
                    action.put("Category Id", (double) category.getId());
                }
                Object categoryIdObj = action.get("Category Id");
                Object campaignIdObj = action.get("Campaign Id");

                if (Objects.equals(action.get("Action").toString(), "Update")) {
                    CategoryBindingRequest request = new CategoryBindingRequest();
                    request.setCategoryId((((Double) categoryIdObj).longValue()));
                    request.setEntityId((((Double) campaignIdObj).longValue()));
                    request.setEntityType(ETypeCategory.CAMPAIGN);
                    if (!this.categoryBindingRepository.existsByEntityIdAndEntityType(request.getEntityId(), ETypeCategory.CAMPAIGN)) {
                        this.categoryBindingService.create(request);
                        System.out.print("Create Campaign-Category Success");
                    } else {
                        this.categoryBindingService.update(request);
                        System.out.print("Update Campaign-Category Success");
                    }
                } else if (Objects.equals(action.get("Action").toString(), "Delete")) {
                    this.categoryBindingService.deleteCategoryAccount((((Double) action.get("Campaign Id")).longValue()), ETypeCategory.ACCOUNT);
                    System.out.print("Delete Campaign-Category Success");
                }
            }
        }
    }
    public void actionWithAccountCategory(Sheet sheet, Map<String, Integer> columnMap2) throws IdInvalidException {
        System.out.println("--------------Action List------------");
        List<Map<String, Object>> listActions = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if(row == null || row.getCell(columnMap2.get("Action")) == null) {
                break;
            }
            Map<String, Object> action = new HashMap<>();
            // Table 2
            Cell accountId = row.getCell(columnMap2.get("Account Id"));
            if(accountId != null) {
                action.put("Account Id", accountId.getNumericCellValue());
            }

            Cell categoryId = row.getCell(columnMap2.get("Category Id"));
            if(categoryId != null) {
                action.put("Category Id", categoryId.getNumericCellValue());
            }

            Cell categoryName = row.getCell(columnMap2.get("Category Name"));
            if(categoryName != null) {
                action.put("Category Name", categoryName.getStringCellValue());
            }

            Cell actionAccountCategory = row.getCell(columnMap2.get("Action"));
            if(actionAccountCategory != null) {
                action.put("Action", actionAccountCategory.getStringCellValue());
            }
            listActions.add(action);
        }

        // Giả sử bạn lấy được giá trị từ listActions là Double
        for(Map<String, Object> action : listActions) {
            if(action.get("Action") == "None") {
                continue;
            }
            if(Objects.equals(action.get("Action").toString(), "Update")) {
                if(action.get("Category Id") == null) {
                    Category category = this.categoryService.getCategoryByName((String) action.get("Category Name"));
                    action.put("Category Id", (double) category.getId());
                }
                Object categoryIdObj = action.get("Category Id");
                Object accountIdObj = action.get("Account Id");

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
                this.categoryBindingService.deleteCategoryAccount((((Double) action.get("Account Id")).longValue()), ETypeCategory.ACCOUNT);
                System.out.print("Delete Account-Category Success");
            }
        }
    }

    public List<ResCategoryInExcel> getCategory(ETypeCategory typeCategory) {
        List<ResCategoryInExcel> result = new ArrayList<>();
        List<Anken> ankenList = this.ankenService.getAll();
        for(Anken anken : ankenList) {
            for(Category category : anken.getCategoryList()) {
                if(category.getTypeCategory() == typeCategory) {
                    result.add(new ResCategoryInExcel(anken.getName(), category.getId(), category.getName(), category.getBudget(),
                            category.getKpiType(), category.getKpiGoal(), category.getStartDate(), category.getEndDate()));
                }
            }
        }
        for (ResCategoryInExcel resCategoryInExcel : result) {
            System.out.println(resCategoryInExcel.toString());
        }
        return result;
    }

    public List<AccountCategoryDTO> getAccountCategory() {
        List<Object[]> results = categoryBindingRepository.findAccountCategoryDetailsRaw();
        List<AccountCategoryDTO> dtos = new ArrayList<>();

        for (Object[] result : results) {
            String ankenName = (String) result[0];
            Long accountId = ((Number) result[1]).longValue();
            String accountName = (String) result[2];
            String accountCode = (String) result[3];
            String media = (String) result[4];
            Long categoryId = (result[5] != null) ? ((Number) result[5]).longValue() : null;
            String categoryName = (String) result[6];
            AccountCategoryDTO dto = new AccountCategoryDTO(ankenName, accountId, accountName, accountCode, media, categoryId, categoryName);
            dtos.add(dto);
        }
        return dtos;
    }

    public List<CampaignCategoryDTO> getCampaignCategoryDetails() {
        List<Object[]> results = categoryBindingRepository.findCampaignCategoryDetailsRaw();
        List<CampaignCategoryDTO> dtos = new ArrayList<>();

        for (Object[] result : results) {
            String ankenName = (String) result[0];
            String accountName = (String) result[1];
            String accountCode = (String) result[2];
            String media = (String) result[3];
            Long campaignId = ((Number) result[4]).longValue();
            String campaignName = (String) result[5];
            String campaignCode = (String) result[6];
            Long categoryId = (result[7] != null) ? ((Number) result[7]).longValue() : null;
            String categoryName = (String) result[8];

            CampaignCategoryDTO dto = new CampaignCategoryDTO(ankenName, accountName, accountCode, media,
                    campaignId, campaignName, campaignCode,
                    categoryId, categoryName);
            dtos.add(dto);
        }
        return dtos;
    }

    public void exportData() {
        try(XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet ankenList = workbook.createSheet("Anken List");
            Sheet accountCategory = workbook.createSheet("Category_Account");
            Sheet campaignCategory = workbook.createSheet("Category_Campaign");
            int ankenListSize = writeSheetAnkenList(ankenList);
            writeSheetAccountCategory(workbook, accountCategory, ankenListSize);
            writeSheetCampaignCategory(workbook, campaignCategory, ankenListSize);
            // Write to an output file
            try (FileOutputStream fileOut = new FileOutputStream("example_with_multiple_tables.xlsx")) {
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Close the workbook
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Excel file with multiple tables created successfully!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int writeSheetAnkenList(Sheet sheet) {
        List<Anken> ankenList = this.ankenService.getAll();
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Anken Id");
        headerRow.createCell(1).setCellValue("Anken Name");
        int cnt = 1;
        for(Anken anken : ankenList) {
            Row row = sheet.createRow(cnt++);
            row.createCell(0).setCellValue(anken.getId());
            row.createCell(1).setCellValue(anken.getName());
        }
        return ankenList.size();
    }
    public void writeSheetAccountCategory(XSSFWorkbook workbook, Sheet sheet, int ankenListSize) {
        int index = 0;
        Map<String, Integer> table1 = new HashMap<>();
        Map<String, Integer> table2 = new HashMap<>();
        String[] tableCategoryHeader = {"Anken Name", "Category Id", "Category Name", "Budget", "Type of KPI",
        "KPI Goal", "Start Date", "End Date", "Action"};
        String[] tableCategoryAccountHeader = {"Anken Name", "Account Id", "Account Name", "Account Code",
        "Media", "Category Id", "Category Name", "Action"};
        for(String s : tableCategoryHeader) {
            table1.put(s, index++);
        }
        index++;
        for(String s : tableCategoryAccountHeader) {
            table2.put(s, index++);
        }
        // Table 1
        Row headerRow = sheet.createRow(0);
        for(Map.Entry<String, Integer> entry : table1.entrySet()) {
            Cell headerCell = headerRow.createCell(entry.getValue());
            headerCell.setCellValue(entry.getKey());
        }
        // Table 2
        for(Map.Entry<String, Integer> entry : table2.entrySet()) {
            Cell headerCell = headerRow.createCell(entry.getValue());
            headerCell.setCellValue(entry.getKey());
        }

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd"));

        int rowCount = 1;
        List<ResCategoryInExcel> listCategory = this.getCategory(ETypeCategory.ACCOUNT);
        List<AccountCategoryDTO> listAccountCategory = this.getAccountCategory();
        int rowMax = Math.max(listCategory.size(), listAccountCategory.size());
        for(int i = 1; i <= rowMax; i++) {
            Row row = sheet.createRow(rowCount);
            if(rowCount <= listCategory.size()) {
                ResCategoryInExcel data = listCategory.get(rowCount - 1);
                LocalDate startDate = data.getStartDate(); // Example LocalDate
                LocalDate endDate = data.getEndDate();

                if(startDate != null) {
                    Date startDateUtil = java.util.Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    Cell startDateCell = row.createCell(table1.get("Start Date"));
                    startDateCell.setCellStyle(dateCellStyle);
                    startDateCell.setCellValue(startDateUtil);
                }

                if(endDate != null) {
                    Date endDateUtil = java.util.Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    Cell endDateCell = row.createCell(table1.get("End Date"));
                    endDateCell.setCellValue(endDateUtil);
                    endDateCell.setCellStyle(dateCellStyle);
                }

                row.createCell(table1.get("Anken Name")).setCellValue(data.getNameAnken());
                row.createCell(table1.get("Category Id")).setCellValue(data.getCategoryId());
                row.createCell(table1.get("Category Name")).setCellValue(data.getCategoryName());
                row.createCell(table1.get("Budget")).setCellValue(data.getBudget());
                row.createCell(table1.get("KPI Goal")).setCellValue(data.getKpiGoal());
                String typeOfKPI = (data.getTypeOfKPI() != null) ? data.getTypeOfKPI().toString() : "";
                row.createCell(table1.get("Type of KPI")).setCellValue(typeOfKPI);
                row.createCell(table1.get("Action")).setCellValue("None");
            }

            if(rowCount <= listAccountCategory.size()) {
                AccountCategoryDTO accountCategoryDTO = listAccountCategory.get(rowCount - 1);
                row.createCell(table2.get("Anken Name")).setCellValue(accountCategoryDTO.getAnkenName());
                row.createCell(table2.get("Account Id")).setCellValue(accountCategoryDTO.getAccountId());
                row.createCell(table2.get("Account Name")).setCellValue(accountCategoryDTO.getAccountName());
                row.createCell(table2.get("Account Code")).setCellValue(accountCategoryDTO.getAccountCode());
                row.createCell(table2.get("Media")).setCellValue(accountCategoryDTO.getMedia());
                if(accountCategoryDTO.getCategoryId() != null) {
                    row.createCell(table2.get("Category Id")).setCellValue(accountCategoryDTO.getCategoryId());
                }
                row.createCell(table2.get("Category Name")).setCellValue(accountCategoryDTO.getCategoryName());
                row.createCell(table2.get("Action")).setCellValue("None");
            }
            rowCount++;
        }

        // Đặt tên cho vùng dữ liệu, để sử dụng trong validation
        Name name = workbook.createName();
        name.setNameName("AnkenList");
        name.setRefersToFormula("Anken List!$B$2:$B$" + ankenListSize + 1);
        // Thêm dữ liệu Validation vào các ô muốn tạo dropdown (ví dụ: B1 tới B10)
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraintAnkenName = validationHelper.createFormulaListConstraint("AnkenList");

        CellRangeAddressList addressList = new CellRangeAddressList(1, listCategory.size(),0 , 0); // B1 to B10
        DataValidation validation = validationHelper.createValidation(constraintAnkenName, addressList);
        sheet.addValidationData(validation);

        DataValidationConstraint constraintActionValues = validationHelper.createExplicitListConstraint(actionsValues);
        addressList = new CellRangeAddressList(1, listCategory.size(), table1.get("Action") , table1.get("Action")); // B1 to B10
        validation = validationHelper.createValidation(constraintActionValues, addressList);
        sheet.addValidationData(validation);

        addressList = new CellRangeAddressList(1, listAccountCategory.size(), table2.get("Action") , table2.get("Action")); // B1 to B10
        validation = validationHelper.createValidation(constraintActionValues, addressList);
        sheet.addValidationData(validation);
    }

    public void writeSheetCampaignCategory(XSSFWorkbook workbook, Sheet sheet, int ankenListSize) {
        int index = 0;
        Map<String, Integer> table1 = new HashMap<>();
        Map<String, Integer> table2 = new HashMap<>();
        String[] tableCategoryHeader = {"Anken Name", "Category Id", "Category Name", "Budget", "Type of KPI",
                "KPI Goal", "Start Date", "End Date", "Action"};
        String[] tableCampaignCategoryHeader = {"Anken Name", "Account Name", "Account Code",
                "Media", "Campaign Id","Campaign Name", "Campaign Code", "Category Id", "Category Name", "Action"};
        for(String s : tableCategoryHeader) {
            table1.put(s, index++);
        }
        index++;
        for(String s : tableCampaignCategoryHeader) {
            table2.put(s, index++);
        }
        // Table 1
        Row headerRow = sheet.createRow(0);
        for(Map.Entry<String, Integer> entry : table1.entrySet()) {
            Cell headerCell = headerRow.createCell(entry.getValue());
            headerCell.setCellValue(entry.getKey());
        }
        // Table 2
        for(Map.Entry<String, Integer> entry : table2.entrySet()) {
            Cell headerCell = headerRow.createCell(entry.getValue());
            headerCell.setCellValue(entry.getKey());
        }

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd"));

        int rowCount = 1;
        List<ResCategoryInExcel> listCategory = this.getCategory(ETypeCategory.CAMPAIGN);
        List<CampaignCategoryDTO> listCampaignCategory = this.getCampaignCategoryDetails();
        int rowMax = Math.max(listCategory.size(), listCampaignCategory.size());
        for(int i = 1; i <= rowMax; i++) {
            Row row = sheet.createRow(rowCount);
            if(rowCount <= listCategory.size()) {
                ResCategoryInExcel data = listCategory.get(rowCount - 1);
                LocalDate startDate = data.getStartDate(); // Example LocalDate
                LocalDate endDate = data.getEndDate();

                if(startDate != null) {
                    Date startDateUtil = java.util.Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    Cell startDateCell = row.createCell(table1.get("Start Date"));
                    startDateCell.setCellStyle(dateCellStyle);
                    startDateCell.setCellValue(startDateUtil);
                }

                if(endDate != null) {
                    Date endDateUtil = java.util.Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    Cell endDateCell = row.createCell(table1.get("End Date"));
                    endDateCell.setCellValue(endDateUtil);
                    endDateCell.setCellStyle(dateCellStyle);
                }

                row.createCell(table1.get("Anken Name")).setCellValue(data.getNameAnken());
                row.createCell(table1.get("Category Id")).setCellValue(data.getCategoryId());
                row.createCell(table1.get("Category Name")).setCellValue(data.getCategoryName());
                row.createCell(table1.get("Budget")).setCellValue(data.getBudget());
                row.createCell(table1.get("KPI Goal")).setCellValue(data.getKpiGoal());
                String typeOfKPI = (data.getTypeOfKPI() != null) ? data.getTypeOfKPI().toString() : "";
                row.createCell(table1.get("Type of KPI")).setCellValue(typeOfKPI);
                row.createCell(table1.get("Action")).setCellValue("None");
            }

            if(rowCount <= listCampaignCategory.size()) {
                CampaignCategoryDTO campaignCategoryDTO = listCampaignCategory.get(rowCount - 1);
                row.createCell(table2.get("Anken Name")).setCellValue(campaignCategoryDTO.getAnkenName());
                row.createCell(table2.get("Account Name")).setCellValue(campaignCategoryDTO.getAccountName());
                row.createCell(table2.get("Account Code")).setCellValue(campaignCategoryDTO.getAccountCode());
                row.createCell(table2.get("Media")).setCellValue(campaignCategoryDTO.getMedia());
                row.createCell(table2.get("Campaign Id")).setCellValue(campaignCategoryDTO.getCampaignId());
                row.createCell(table2.get("Campaign Name")).setCellValue(campaignCategoryDTO.getCampaignName());
                row.createCell(table2.get("Campaign Code")).setCellValue(campaignCategoryDTO.getCampaignName());
                if(campaignCategoryDTO.getCategoryId() != null) {
                    row.createCell(table2.get("Category Id")).setCellValue(campaignCategoryDTO.getCategoryId());
                }
                row.createCell(table2.get("Category Name")).setCellValue(campaignCategoryDTO.getCategoryName());
                row.createCell(table2.get("Action")).setCellValue("None");
            }
            rowCount++;
        }
        // Đặt tên cho vùng dữ liệu, để sử dụng trong validation
//        Name name = workbook.createName();
//        name.setNameName("AnkenList");
//        name.setRefersToFormula("Anken List!$B$2:$B$" + ankenListSize + 1);
        // Thêm dữ liệu Validation vào các ô muốn tạo dropdown (ví dụ: B1 tới B10)
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraintAnkenName = validationHelper.createFormulaListConstraint("AnkenList");

        CellRangeAddressList addressList = new CellRangeAddressList(1, listCategory.size(),0 , 0); // B1 to B10
        DataValidation validation = validationHelper.createValidation(constraintAnkenName, addressList);
        sheet.addValidationData(validation);

        DataValidationConstraint constraintActionValues = validationHelper.createExplicitListConstraint(actionsValues);
        addressList = new CellRangeAddressList(1, listCategory.size(), table1.get("Action") , table1.get("Action")); // B1 to B10
        validation = validationHelper.createValidation(constraintActionValues, addressList);
        sheet.addValidationData(validation);

        addressList = new CellRangeAddressList(1, listCampaignCategory.size(), table2.get("Action") , table2.get("Action")); // B1 to B10
        validation = validationHelper.createValidation(constraintActionValues, addressList);
        sheet.addValidationData(validation);
    }
}
