package com.iemr.flw.integration.provider.emrlite;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.DiagnosticProviderToken;
import com.iemr.flw.integration.provider.emrlite.dto.EmrLiteLoginRequest;
import com.iemr.flw.integration.provider.emrlite.dto.EmrLiteLoginResponse;
import com.iemr.flw.integration.provider.emrlite.dto.EmrLiteProviderResponse;
import com.iemr.flw.integration.provider.emrlite.dto.EmrLiteRefreshRequest;
import com.iemr.flw.integration.provider.emrlite.dto.EmrLiteRefreshResponse;
import com.iemr.flw.masterEnum.DiagnosticProviderCode;
import com.iemr.flw.repo.iemr.DiagnosticProviderTokenRepo;
import com.iemr.flw.utils.CryptoUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

@Component
public class EmrLiteTokenManager {

    private static final Logger logger = LoggerFactory.getLogger(EmrLiteTokenManager.class);
    private static final String PROVIDER_CODE = DiagnosticProviderCode.EMRLITE.name();
    private static final String TYPE_ACCESS = "ACCESS";
    private static final String TYPE_REFRESH = "REFRESH";

    @Value("${diagnostic.emrlite.login-url}")
    private String loginUrl;

    @Value("${diagnostic.emrlite.refresh-url}")
    private String refreshUrl;

    @Value("${diagnostic.emrlite.username}")
    private String username;

    @Value("${diagnostic.emrlite.password}")
    private String encodedPassword;

    @Value("${diagnostic.emrlite.token-ttl-seconds}")
    private long tokenTtlSeconds;

    @Value("${diagnostic.emrlite.refresh-ttl-seconds}")
    private long refreshTtlSeconds;

    private String password;

    @Autowired
    private DiagnosticProviderTokenRepo tokenRepo;

    @Autowired
    private CryptoUtil cryptoUtil;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    @PostConstruct
    private void init() {
        this.password = encodedPassword != null && encodedPassword.startsWith("0X10:")
                ? new String(Base64.getDecoder().decode(encodedPassword.substring(5)))
                : encodedPassword;
    }

    public String getValidToken() throws Exception {
        Optional<DiagnosticProviderToken> record =
                tokenRepo.findByProviderCodeAndTokenType(PROVIDER_CODE, TYPE_ACCESS);
        if (record.isPresent()) {
            DiagnosticProviderToken token = record.get();
            boolean expired = token.getExpiresAt() == null
                    || Instant.now().isAfter(token.getExpiresAt().toInstant().minusSeconds(60));
            if (!expired) {
                return cryptoUtil.decrypt(token.getTokenValue());
            }
        }
        // No token in DB or token is expired — acquire lock to prevent parallel logins/refreshes
        synchronized (this) {
            Optional<DiagnosticProviderToken> recheck =
                    tokenRepo.findByProviderCodeAndTokenType(PROVIDER_CODE, TYPE_ACCESS);
            if (recheck.isPresent()) {
                DiagnosticProviderToken t = recheck.get();
                boolean stillExpired = t.getExpiresAt() == null
                        || Instant.now().isAfter(t.getExpiresAt().toInstant().minusSeconds(60));
                if (!stillExpired) {
                    return cryptoUtil.decrypt(t.getTokenValue());
                }
            }
            return refreshOrLogin();
        }
    }

    public synchronized String forceRefresh() throws Exception {
        return refreshOrLogin();
    }

    private String refreshOrLogin() throws Exception {
        try {
            return refreshAccessToken();
        } catch (Exception e) {
            logger.warn("Token refresh failed ({}), falling back to full login", e.getMessage());
            return login();
        }
    }

    private String refreshAccessToken() throws Exception {
        Optional<DiagnosticProviderToken> refreshRecord =
                tokenRepo.findByProviderCodeAndTokenType(PROVIDER_CODE, TYPE_REFRESH);
        if (refreshRecord.isEmpty()) {
            throw new Exception("No refresh token in DB — need full login");
        }
        DiagnosticProviderToken refreshToken = refreshRecord.get();
        // Same expiry-with-safety-buffer check already applied to the access token in
        // getValidToken() — no point sending the provider a refresh token we already know has
        // expired; fail fast so refreshOrLogin() falls back to a full login immediately.
        boolean refreshExpired = refreshToken.getExpiresAt() == null
                || Instant.now().isAfter(refreshToken.getExpiresAt().toInstant().minusSeconds(60));
        if (refreshExpired) {
            throw new Exception("Refresh token expired — need full login");
        }
        String rawRefresh = cryptoUtil.decrypt(refreshToken.getTokenValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(gson.toJson(new EmrLiteRefreshRequest(rawRefresh)), headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(refreshUrl, HttpMethod.POST, entity, String.class);
        } catch (HttpStatusCodeException e) {
            String body = e.getResponseBodyAsString();
            logger.warn("EMR Lite refresh rejected by provider: url={}, status={}, body={}",
                    refreshUrl, e.getStatusCode(), body);
            throw new Exception("Refresh token rejected by provider (HTTP " + e.getStatusCode() + ") - " + body, e);
        } catch (RestClientException e) {
            logger.warn("EMR Lite refresh request failed (network/connection error): url={}", refreshUrl, e);
            throw new Exception("EMR Lite refresh request failed: " + e.getMessage(), e);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            logger.warn("EMR Lite refresh failed: url={}, status={}, body={}",
                    refreshUrl, response.getStatusCode(), response.getBody());
            throw new Exception("Token refresh failed with status: " + response.getStatusCode());
        }

        EmrLiteProviderResponse envelope = gson.fromJson(response.getBody(), EmrLiteProviderResponse.class);
        if (envelope == null) {
            logger.warn("EMR Lite refresh response could not be parsed: url={}, rawBody={}", refreshUrl, response.getBody());
            throw new Exception("EMR Lite refresh response could not be parsed, rawBody=" + response.getBody());
        }
        if (!envelope.isSuccess()) {
            logger.warn("EMR Lite refresh rejected: url={}, message={}, rawBody={}",
                    refreshUrl, envelope.getMessage(), response.getBody());
            throw new Exception("Token refresh rejected: " + envelope.getMessage());
        }

        EmrLiteRefreshResponse refreshResponse = gson.fromJson(envelope.getData(), EmrLiteRefreshResponse.class);
        if (refreshResponse.getAccess() == null) {
            throw new Exception("Refresh response returned null access token");
        }

        persistToken(TYPE_ACCESS, refreshResponse.getAccess(),
                Timestamp.from(Instant.now().plus(tokenTtlSeconds, ChronoUnit.SECONDS)));

        logger.info("EMR Lite access token refreshed, TTL {}s", tokenTtlSeconds);
        return refreshResponse.getAccess();
    }

    private String login() throws Exception {
        logger.info("Attempting EMR Lite login: url={}, username={}", loginUrl, username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                gson.toJson(new EmrLiteLoginRequest(username, password)), headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, String.class);
        } catch (HttpStatusCodeException e) {
            String body = e.getResponseBodyAsString();
            logger.error("EMR Lite login rejected by provider: url={}, status={}, body={}",
                    loginUrl, e.getStatusCode(), body);
            throw new Exception("EMR Lite login failed: HTTP " + e.getStatusCode() + " - " + body, e);
        } catch (RestClientException e) {
            logger.error("EMR Lite login request failed (network/connection error): url={}", loginUrl, e);
            throw new Exception("EMR Lite login request failed: " + e.getMessage(), e);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            logger.error("EMR Lite login failed: url={}, status={}, body={}",
                    loginUrl, response.getStatusCode(), response.getBody());
            throw new Exception("EMR Lite login failed with status: " + response.getStatusCode());
        }

        EmrLiteProviderResponse envelope = gson.fromJson(response.getBody(), EmrLiteProviderResponse.class);
        if (envelope == null) {
            logger.error("EMR Lite login response could not be parsed: url={}, rawBody={}", loginUrl, response.getBody());
            throw new Exception("EMR Lite login response could not be parsed, rawBody=" + response.getBody());
        }
        if (!envelope.isSuccess()) {
            logger.error("EMR Lite login rejected: url={}, message={}, rawBody={}",
                    loginUrl, envelope.getMessage(), response.getBody());
            throw new Exception("EMR Lite login rejected: " + envelope.getMessage());
        }

        EmrLiteLoginResponse tokens = gson.fromJson(envelope.getData(), EmrLiteLoginResponse.class);
        if (tokens == null || tokens.getAccessToken() == null) {
            logger.error("EMR Lite login returned no accessToken: url={}, rawBody={}", loginUrl, response.getBody());
            throw new Exception("Login returned null accessToken");
        }

        persistToken(TYPE_ACCESS, tokens.getAccessToken(),
                Timestamp.from(Instant.now().plus(tokenTtlSeconds, ChronoUnit.SECONDS)));
        if (tokens.getRefreshToken() != null) {
            persistToken(TYPE_REFRESH, tokens.getRefreshToken(),
                    Timestamp.from(Instant.now().plus(refreshTtlSeconds, ChronoUnit.SECONDS)));
        }

        logger.info("EMR Lite login successful, access TTL {}s, refresh TTL {}s", tokenTtlSeconds, refreshTtlSeconds);
        return tokens.getAccessToken();
    }

    private void persistToken(String tokenType, String rawValue, Timestamp expiresAt) throws Exception {
        String encrypted = cryptoUtil.encrypt(rawValue);
        Optional<DiagnosticProviderToken> existing =
                tokenRepo.findByProviderCodeAndTokenType(PROVIDER_CODE, tokenType);
        DiagnosticProviderToken record = existing.orElseGet(DiagnosticProviderToken::new);
        record.setProviderCode(PROVIDER_CODE);
        record.setTokenType(tokenType);
        record.setTokenValue(encrypted);
        record.setExpiresAt(expiresAt);
        tokenRepo.save(record);
    }
}
