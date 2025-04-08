package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.GeneralOpdDto;
import com.iemr.flw.service.GeneralOpdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/general-opd")
public class GeneralOPDController {

    @Autowired
    private GeneralOpdService generalOpdService;

    @GetMapping("/beneficiaries/{ashaId}")
    public ResponseEntity<List<GeneralOpdDto>> getOpdBeneficiaries(@PathVariable String ashaId) {
        return ResponseEntity.ok(generalOpdService.getOpdListForAsha(ashaId));
    }
} 