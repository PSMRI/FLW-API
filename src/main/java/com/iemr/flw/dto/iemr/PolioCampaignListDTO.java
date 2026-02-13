package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
public class PolioCampaignListDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("children_vaccinated")
    private String numberOfChildren;

    @JsonProperty("campaign_photos")
    private List<String> campaignPhotos;


}
