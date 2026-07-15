package com.iemr.flw.integration.provider.emrlite.dto;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic envelope returned by every provider endpoint: {"Result":"Success|Failure","Data":{...},"Message":"..."}
 */
@Data
@NoArgsConstructor
public class EmrLiteProviderResponse {
    private String Result;
    private JsonObject Data;
    private String Message;

    public boolean isSuccess() {
        return "Success".equalsIgnoreCase(Result);
    }
}
