package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrsCampaignListResponseDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonProperty("start_date")
    private LocalDate StartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonProperty("end_date")
    private LocalDate EndDate;

    @JsonProperty("number_of_families")
    private Double NumberOfFamilies;

    @JsonProperty("campaign_photos")
    private List<String> campaignPhotos;
}