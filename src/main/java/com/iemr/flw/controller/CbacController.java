package com.iemr.flw.controller;

import com.iemr.flw.service.CbacService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/couple", headers = "Authorization")
public class CbacController {

    private final Logger logger = LoggerFactory.getLogger(CoupleController.class);

    @Autowired
    private CbacService cbacService;


}
