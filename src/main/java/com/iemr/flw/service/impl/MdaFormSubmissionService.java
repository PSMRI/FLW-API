package com.iemr.flw.service.impl;

import java.util.List;

import com.iemr.flw.dto.iemr.MdaFormSubmissionRequest;
import com.iemr.flw.dto.iemr.MdaFormSubmissionResponse;

public interface MdaFormSubmissionService {
    String saveFormData(List<MdaFormSubmissionRequest> requests);

    List<MdaFormSubmissionResponse> getFormDataByUserName(String  userName);
}
