package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
public class PolioCampaignListResponseDTO {
    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("children_vaccinated")
    private Double numberOfChildren;

    @JsonProperty("campaign_photos")
    private List<String> campaignPhotos;


}
