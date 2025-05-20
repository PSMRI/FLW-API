package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VillageLevelFormService {

    String submitForm(VhndDto dto);

    String submitVhncForm(VhncDto dto);

    String submitPhcForm(PhcReviewMeetingDTO dto);

    String submitAhdForm(AhdMeetingDto dto);

    String submitDewormingForm(DewormingDto dto);

    List<?> getAll(GetVillageLevelRequestHandler getVillageLevelRequestHandler);
}