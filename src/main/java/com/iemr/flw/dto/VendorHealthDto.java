package com.iemr.flw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorHealthDto {
    private boolean isConnected;
    private boolean isDeviceIntegrated;
}
