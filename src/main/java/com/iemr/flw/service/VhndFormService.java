package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.VillageFormEntry;
import com.iemr.flw.dto.iemr.VhndDto;
import com.iemr.flw.dto.iemr.VilageLevelFormDto;
import com.iemr.flw.repo.iemr.VillageFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface  VhndFormService  {


    public String submitForm(VhndDto dto) ;
    public List<VillageFormEntry> getAll(Integer userId);
}
