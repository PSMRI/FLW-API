package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.VillageFormEntry;
import com.iemr.flw.dto.iemr.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface  VhndFormService  {

    public String submitForm(VhndDto dto);
    public String  submitVhncForm(VhncDto dto);
    public String  submitPhcForm(PhcReviewMeetingDTO dto);
    public String  submitAdhForm(AhdMeetingDto dto);
    public String  submitDewormingForm(DewormingDto dto);
    public Object getAll(GetVillageLevelRequestHandler getVillageLevelRequestHandler);
}