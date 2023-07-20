package com.iemr.flw.service;


import org.springframework.stereotype.Component;

@Component
public interface TBSuspectedService {

    String getByBenId(Long benId, String authorisation) throws Exception;

    String save() throws Exception;

}
