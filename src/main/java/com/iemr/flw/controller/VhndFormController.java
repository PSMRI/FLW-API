package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.VhndFormDto;
import com.iemr.flw.service.VhndFormService;
import com.iemr.flw.service.impl.VhndFormServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forms/vhnd")
public class VhndFormController {

    @Autowired
    private VhndFormService vhndFormService;

    @PostMapping
    public ResponseEntity<?> submitVhndForm(@RequestBody VhndFormDto dto) {
        return vhndFormService.submitForm(dto);
    }
}