package com.example.ProjectDAC.service;

import com.example.ProjectDAC.repository.CampaignRepository;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;
    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }
    public boolean isExistedCampaignById(long id) {
        return this.campaignRepository.existsById(id);
    }
}
