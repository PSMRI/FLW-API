package com.iemr.flw.integration.provider.emrlite.dto;

import com.google.gson.JsonElement;
import lombok.Data;
import lombok.NoArgsConstructor;

// Data is JsonElement, not JsonObject, since the API sometimes returns it as a plain string
// (e.g. error cases) instead of an object.
@Data
@NoArgsConstructor
public class EmrLiteProviderResponse {
    private String Result;
    private JsonElement Data;
    private String Message;

    public boolean isSuccess() {
        return "Success".equalsIgnoreCase(Result);
    }
}
