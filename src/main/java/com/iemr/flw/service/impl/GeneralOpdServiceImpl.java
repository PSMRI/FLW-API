package com.iemr.flw.service.impl;

import com.google.gson.*;
import com.iemr.flw.domain.identity.*;
import com.iemr.flw.domain.iemr.BenVisitDetail;
import com.iemr.flw.domain.iemr.GeneralOpdEntry;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.GeneralOpdDto;
import com.iemr.flw.mapper.InputMapper;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.identity.HouseHoldRepo;
import com.iemr.flw.repo.iemr.BenVisitDetailsRepo;
import com.iemr.flw.repo.iemr.BeneficiaryRepository;
import com.iemr.flw.repo.iemr.GeneralOpdRepo;
import com.iemr.flw.service.GeneralOpdService;
import com.iemr.flw.utils.config.ConfigProperties;
import com.iemr.flw.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Date;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GeneralOpdServiceImpl implements GeneralOpdService {

    private final Logger logger = LoggerFactory.getLogger(BeneficiaryServiceImpl.class);
    @Value("${door-to-door-page-size}")
    private String door_to_door_page_size;
    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private HouseHoldRepo houseHoldRepo;

    @Autowired
    private BenVisitDetailsRepo benVisitDetailsRepo;

    @Autowired
    private GeneralOpdRepo generalOpdRepo;


    @Override
    public String  getOpdListForAsha(GetBenRequestHandler request, String authorisation) throws Exception {

      return   generalOpdRepo.findAll().toString();
    }
}