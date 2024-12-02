package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Campaign;
import com.example.ProjectDAC.repository.CampaignRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;
    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }
    public boolean isExistedCampaignById(long id) {
        return this.campaignRepository.existsById(id);
    }

    public boolean checkPermission(long campaignId, List<Long> ids) {
        Campaign campaign = this.campaignRepository.findById(campaignId);
        if(campaign.getAccount().getAnken() == null) {
            return false;
        }
        long ankenId = campaign.getAccount().getAnken().getId();
        return ids != null && ids.contains(ankenId);
    }
}
