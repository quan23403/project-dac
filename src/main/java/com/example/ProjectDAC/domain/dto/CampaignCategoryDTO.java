package com.example.ProjectDAC.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignCategoryDTO {
    private String ankenName;
    private String accountName;
    private String accountCode;
    private String media;
    private Long campaignId;
    private String campaignName;
    private String campaignCode;
    private Long categoryId;
    private String categoryName;

    public CampaignCategoryDTO(String ankenName, String accountName, String accountCode, String media, Long campaignId, String campaignName, String campaignCode, Long categoryId, String categoryName) {
        this.ankenName = ankenName;
        this.accountName = accountName;
        this.accountCode = accountCode;
        this.media = media;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.campaignCode = campaignCode;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public CampaignCategoryDTO() {
    }
}
