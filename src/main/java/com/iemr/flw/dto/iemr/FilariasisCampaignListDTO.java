package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilariasisCampaignListDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("no_of_families")
    private String numberOfFamilies;

    @JsonProperty("no_of_individuals")
    private String numberOfIndividuals;

    @JsonProperty("mda_photos")
    private List<String> mdaPhotos;


}
