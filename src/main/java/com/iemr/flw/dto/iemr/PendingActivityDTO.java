package com.iemr.flw.dto.iemr;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PendingActivityDTO {
    private Long id;
    private Integer userId;
    private List<MultipartFile> images;
    private String moduleName;
    private String activityName;
}
