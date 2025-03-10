package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.MicroBirthPlan;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class MicroBirthPlanDTO implements Serializable {
    Integer  userId;
    List<MicroBirthPlan> entries;

    @Data
    public static class Entry implements Serializable {
        private Integer id;
        private Long benId;
        private String contactNumber1;
        private String contactNumber2;
        private String scHosp;
        private String block;
        private String nearestSc;
        private String nearestPhc;
        private String nearestFru;
        private String usg;
        private String bloodGroup;
        private String bloodDonors2;
        private String birthCompanion;
        private String careTaker;
        private String communityMember;
        private String communityMemberContact;
        private String modeOfTransportation;
    }
}