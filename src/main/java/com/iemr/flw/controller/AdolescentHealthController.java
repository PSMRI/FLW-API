package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.AdolescentHealthDTO;
import com.iemr.flw.service.AdolescentHealthService;
import com.iemr.flw.utils.response.OutputResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/adolescentHealth", headers = "Authorization")

public class AdolescentHealthController {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(AdolescentHealthController.class);

    @Autowired
    private AdolescentHealthService adolescentHealthService;

    @RequestMapping(name = "/savAll", method = RequestMethod.POST,headers = "Authorization")
    public String saveAdolescentHealth(@RequestBody AdolescentHealthDTO adolescentHealthDTO) {
        OutputResponse response = new OutputResponse();
        response.setResponse(adolescentHealthService.saveAll(adolescentHealthDTO));
        try {
            if (adolescentHealthDTO.getAdolescentHealths().size() != 0) {
                String result = adolescentHealthService.saveAll(adolescentHealthDTO);
                if (result != null) {
                    response.setResponse(result);

                }

            } else
                response.setError(500, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in get data : " + e);
            response.setError(500, "Error in get data : " + e);
        }
        return response.toString();

    }

    @RequestMapping(name = "/getAll",method = RequestMethod.POST,headers = "Authorization")
    public String getAllAdolescentHealth(@RequestBody GetBenRequestHandler getBenRequestHandler) {
        OutputResponse response = new OutputResponse();
        try {
            if (adolescentHealthService.getAllAdolescentHealth(getBenRequestHandler).size() != 0) {
                response.setResponse(adolescentHealthService.getAllAdolescentHealth(getBenRequestHandler).toString());


            } else
                response.setError(500, "Invalid/NULL request obj");
        } catch (Exception e) {
            logger.error("Error in get data : " + e);
            response.setError(500, "Error in get data : " + e);
        }
        return response.toString();
    }

}
