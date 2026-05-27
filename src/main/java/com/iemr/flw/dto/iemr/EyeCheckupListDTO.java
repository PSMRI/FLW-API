package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class EyeCheckupListDTO {
    @SerializedName("visit_date")
    private String visit_date;

    @SerializedName("symptoms_observed")
    private Object  symptoms_observed;

    @SerializedName("eye_affected")
    private String eye_affected;

    @SerializedName("referred_to")
    private String referred_to;

    @SerializedName("follow_up_status")
    private String follow_up_status;

    @SerializedName("date_of_surgery")
    private String date_of_surgery;

    @SerializedName("discharge_summary_upload")
    private String discharge_summary_upload;

    public String getSymptomsAsString() {
        if (symptoms_observed == null) return null;

        if (symptoms_observed instanceof List<?>) {
            return ((List<?>) symptoms_observed)
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
        }
        return symptoms_observed.toString();
    }

}
