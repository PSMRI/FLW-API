package com.iemr.flw.service;

import com.iemr.flw.dto.crashlogs.CrashLogRequest;
import org.springframework.web.multipart.MultipartFile;
import com.iemr.flw.utils.exception.IEMRException;

public interface CrashLogService {
    String saveCrashLog(CrashLogRequest request, Integer userId, MultipartFile file) throws IEMRException;
}