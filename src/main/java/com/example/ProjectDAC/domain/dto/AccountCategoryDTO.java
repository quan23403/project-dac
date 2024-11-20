package com.example.ProjectDAC.domain.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCategoryDTO {
    private String ankenName;
    private Long accountId;
    private String accountName;
    private String accountCode;
    private String media;
    private Long categoryId;
    private String categoryName;

    public AccountCategoryDTO(String ankenName, Long accountId, String accountName, String accountCode, String media, Long categoryId, String categoryName) {
        this.ankenName = ankenName;
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountCode = accountCode;
        this.media = media;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public AccountCategoryDTO() {
    }
}
