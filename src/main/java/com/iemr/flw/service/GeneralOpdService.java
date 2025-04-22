package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.GeneralOpdDto;

import java.util.List;

public interface GeneralOpdService {
    String getOpdListForAsha(GetBenRequestHandler getBenRequestHandler,String authorisation) throws Exception;
}