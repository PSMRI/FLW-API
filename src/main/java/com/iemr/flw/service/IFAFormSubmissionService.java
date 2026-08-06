package com.iemr.flw.service;


import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.IFAFormSubmissionRequest;
import com.iemr.flw.dto.iemr.IFAFormSubmissionResponse;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface IFAFormSubmissionService {
    String saveFormData(List<IFAFormSubmissionRequest> requests,Integer userId);
    List<IFAFormSubmissionResponse> getFormData(GetBenRequestHandler getBenRequestHandler);
}
