package com.example.ProjectDAC.repository;

import com.example.ProjectDAC.domain.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Campaign findById(long id);
}
