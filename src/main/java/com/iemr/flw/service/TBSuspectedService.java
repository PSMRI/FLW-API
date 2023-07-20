package com.iemr.flw.service;

public interface TBSuspectedService {

    String getByBenId(Long benId, String authorisation) throws Exception;

    String save() throws Exception;

}
