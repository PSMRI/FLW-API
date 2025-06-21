package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.AshaWorker;
import org.springframework.stereotype.Service;

@Service
public interface AshaProfileService {
    AshaWorker saveEditData(AshaWorker ashaWorkerRequest);

    AshaWorker getProfileData(Integer userId );

}
