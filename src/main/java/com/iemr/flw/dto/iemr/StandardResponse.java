package com.iemr.flw.dto.iemr;

import lombok.Data;
import java.util.List;

@Data
public class StandardResponse<T> {
    private int statusCode;
    private String errorMessage;
    private String status;
    private T data;
}
