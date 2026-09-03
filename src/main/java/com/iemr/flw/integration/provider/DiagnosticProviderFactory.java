package com.iemr.flw.integration.provider;

import com.iemr.flw.masterEnum.DiagnosticOrderType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DiagnosticProviderFactory {

    @Value("${diagnostic.provider.xray}")
    private String xrayProviderCode;

    @Value("${diagnostic.provider.truenat}")
    private String truenatProviderCode;

    @Autowired
    private List<DiagnosticProvider> providers;

    private Map<String, DiagnosticProvider> registry;

    @PostConstruct
    public void init() {
        registry = providers.stream()
                .collect(Collectors.toMap(DiagnosticProvider::getProviderCode, p -> p));
    }

    public DiagnosticProvider getProvider(String providerCode) {
        DiagnosticProvider provider = registry.get(providerCode);
        if (provider == null) {
            throw new IllegalArgumentException("No diagnostic provider registered for code: " + providerCode);
        }
        return provider;
    }

    // XRAY_CHEST is routed to its own vendor; the TrueNat family (MTB/MTB_PLUS/MDR_RIF) shares
    // another, each configurable independently so a new vendor can be swapped in for either group
    // without a code change.
    public String getProviderCodeForOrderType(DiagnosticOrderType orderType) {
        return DiagnosticOrderType.XRAY_CHEST.equals(orderType) ? xrayProviderCode : truenatProviderCode;
    }

    public DiagnosticProvider getProviderForOrderType(DiagnosticOrderType orderType) {
        return getProvider(getProviderCodeForOrderType(orderType));
    }
}
