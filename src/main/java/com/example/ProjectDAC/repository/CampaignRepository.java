package com.example.ProjectDAC.repository;

import com.example.ProjectDAC.domain.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
}
