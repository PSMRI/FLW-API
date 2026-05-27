package com.iemr.flw.dto.iemr;

import com.iemr.flw.utils.PaymentParam;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class IncentiveActivityDTO implements Serializable {

    private Long id;

    private String name;

    private String description;

    private PaymentParam paymentParam;

    private Integer rate;

    private Integer state;

    private Integer district;

    private String group;
    private String  groupName;

    private String fmrCode;

    private String fmrCodeOld;

    private Timestamp createdDate;

    private String createdBy;

    private Timestamp updatedDate;

    private String updatedBy;

}
