package com.iemr.flw.service.impl;

import com.iemr.flw.controller.CoupleController;
import com.iemr.flw.domain.iemr.MalariaFollowUp;
import com.iemr.flw.dto.iemr.MalariaFollowListUpDTO;
import com.iemr.flw.dto.iemr.MalariaFollowUpDTO;
import com.iemr.flw.repo.iemr.MalariaFollowUpRepository;
import com.iemr.flw.service.IncentiveLogicService;
import com.iemr.flw.service.MalariaFollowUpService;
import com.iemr.flw.utils.JwtUtil;
import io.jsonwebtoken.Jwt;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MalariaFollowUpServiceImpl implements MalariaFollowUpService {

    @Autowired
    private MalariaFollowUpRepository repository;

    @Autowired
    private IncentiveLogicService incentiveLogicService;

    private final Logger logger = LoggerFactory.getLogger(MalariaFollowUpServiceImpl.class);

    @Autowired
    private JwtUtil jwtUtil;

    public Boolean saveFollowUp(MalariaFollowUpDTO malariaFollowUpDTO,String token) {
      try {
          for(MalariaFollowListUpDTO dto : malariaFollowUpDTO.getMalariaFollowListUp()){
              if (dto.getTreatmentStartDate().after(new Date())) {
                  return false;
              }

              if (dto.getTreatmentCompletionDate() != null &&
                      dto.getTreatmentCompletionDate().before(dto.getTreatmentStartDate())) {
                  return false;
              }


              dto.setUserId(jwtUtil.extractUserId(token));
              MalariaFollowUp entity = new MalariaFollowUp();
              BeanUtils.copyProperties(dto, entity);
              repository.save(entity);
              if(entity!=null){
                  incentiveLogicService.incentiveForMalariaFollowUp(
                          entity.getBenId(),
                          new Timestamp(entity.getTreatmentStartDate().getTime()),
                          new Timestamp(entity.getTreatmentCompletionDate().getTime()),
                          entity.getUserId()
                  );
              }

              return true;
          }
      }catch (Exception e){
          logger.error("Exception saveFollowUp: "+ e.getMessage());
          return  false;

      }

       return  false;
    }

    public List<MalariaFollowListUpDTO> getByUserId(Integer userId) {
        return repository.findByUserId(userId).stream().map(entity -> {
            MalariaFollowListUpDTO dto = new MalariaFollowListUpDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }

}
