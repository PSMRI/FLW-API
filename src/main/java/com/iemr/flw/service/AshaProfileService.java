package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.AshaWorker;
import org.springframework.stereotype.Service;

@Service
public interface AshaProfileService {
    AshaWorker saveEditData(AshaWorker editdata);
    AshaWorker getProfileData(Integer employeeId);

}
