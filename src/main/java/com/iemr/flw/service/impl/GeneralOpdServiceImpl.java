/*
* AMRIT – Accessible Medical Records via Integrated Technology
* Integrated EHR (Electronic Health Records) Solution
*
* Copyright (C) "Piramal Swasthya Management and Research Institute"
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
* @@ -0,0 +1,212 @@
/*
* AMRIT – Accessible Medical Records via Integrated Technology
*/
package com.iemr.flw.service.impl;

import com.iemr.flw.domain.identity.*;
import com.iemr.flw.domain.iemr.GeneralOpdData;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.GeneralOpdRepo;
import com.iemr.flw.service.GeneralOpdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
    private GeneralOpdRepo generalOpdRepo;

    @Override
    public Object getOpdListForAsha(GetBenRequestHandler request, String authorisation) throws Exception {
        List<GeneralOpdData> filteredList;

        do {
            filteredList = generalOpdRepo.findAll().stream()
                    .filter(generalOpdData ->
                            generalOpdData.getVisitCategory() != null &&
                                    generalOpdData.getVisitCategory().equals("General OPD") && generalOpdData.getVisitCode()!=0)
                    .collect(Collectors.toList());


        } while (filteredList.isEmpty());

        return filteredList.stream().filter(data-> data.getAgentId().equals(request.getUserName())).collect(Collectors.toList());
    }
}