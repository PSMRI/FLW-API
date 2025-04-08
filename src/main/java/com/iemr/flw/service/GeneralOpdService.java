package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.GeneralOpdDto;

import java.util.List;

public interface GeneralOpdService {
    List<GeneralOpdDto> getOpdListForAsha(String ashaId);
}