package com.iemr.flw.integration.provider;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DiagnosticProviderFactory {

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
}
