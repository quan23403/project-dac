package com.example.ProjectDAC.util.constant;

public enum ESheetTemplateExcel {
    ANKEN_LIST(0),
    CATEGORY_ACCOUNT(1),
    CATEGORY_CAMPAIGN(2);

    private final int sheetNumber;
    ESheetTemplateExcel(int sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public int getSheetNumber() {
        return sheetNumber;
    }
}
