package com.iemr.flw.service.impl;

import com.iemr.flw.dto.CbacDTO;
import com.iemr.flw.service.CbacService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CbacServiceImpl implements CbacService {

    public List<CbacDTO> getCbacList() {
        return new ArrayList<>();
    }
}
