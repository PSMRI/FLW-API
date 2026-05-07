package com.iemr.flw.service.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.flw.domain.iemr.BenFlowStatus;
import com.iemr.flw.domain.iemr.StopTBRegistration;
import com.iemr.flw.dto.iemr.StopTBRegistrationDto;
import com.iemr.flw.repo.iemr.BenFlowStatusRepo;
import com.iemr.flw.repo.iemr.StopTBRegistrationRepo;
import com.iemr.flw.service.StopTBService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StopTBServiceImpl implements StopTBService {

    private final Logger logger = LoggerFactory.getLogger(StopTBServiceImpl.class);

    @Autowired
    private StopTBRegistrationRepo stopTBRegistrationRepo;

    @Autowired
    private BenFlowStatusRepo benFlowStatusRepo;

    @Value("${tm-url}")
    private String tmUrl;

    @Override
    @Transactional
    public Map<String, Object> saveRegistration(String requestBody, String authorization) throws Exception {
        Map<String, Object> result = new HashMap<>();

        // Parse incoming JSON
        JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

        // Extract Stop TB specific fields
        String personFrom = getStringField(jsonObject, "personFrom");
        String caseFindingType = getStringField(jsonObject, "caseFindingType");
        Integer tuId = getIntField(jsonObject, "tuId");
        String tuName = getStringField(jsonObject, "tuName");
        Integer healthFacilityId = getIntField(jsonObject, "healthFacilityId");
        String healthFacilityName = getStringField(jsonObject, "healthFacilityName");
        Double weight = getDoubleField(jsonObject, "weight");
        Double height = getDoubleField(jsonObject, "height");
        Double bmi = getDoubleField(jsonObject, "bmi");
        Double temperatureValue = getDoubleField(jsonObject, "temperatureValue");
        String createdBy = getStringField(jsonObject, "createdBy");
        Integer providerServiceMapID = getIntField(jsonObject, "providerServiceMapID");

        // Remove Stop TB fields — forward clean TM-API request
        jsonObject.remove("personFrom");
        jsonObject.remove("caseFindingType");
        jsonObject.remove("tuId");
        jsonObject.remove("tuName");
        jsonObject.remove("healthFacilityId");
        jsonObject.remove("healthFacilityName");
        jsonObject.remove("weight");
        jsonObject.remove("height");
        jsonObject.remove("bmi");
        jsonObject.remove("temperatureValue");

        // Prevent TM-API from creating its own flow record — we create Stop TB specific one
        jsonObject.addProperty("isMobile", true);

        // Step 1 — forward to TM-API for standard beneficiary registration
        // Extract auth headers from current request — same pattern as FLW-API
        String jwtToken = authorization;
        String cookieValue = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest httpRequest = attributes.getRequest();
            if (httpRequest.getCookies() != null) {
                for (Cookie cookie : httpRequest.getCookies()) {
                    if ("Jwttoken".equals(cookie.getName())) {
                        cookieValue = cookie.getValue();
                    }
                }
            }
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", jwtToken);
        headers.set("User-Agent", "okhttp");

        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
        String tmRegistrationUrl = tmUrl + "/registrar/registrarBeneficaryRegistrationNew";

        ResponseEntity<Map> response = restTemplate.exchange(tmRegistrationUrl, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new Exception("Beneficiary registration failed in TM-API");
        }

        Map responseBody = response.getBody();
        // TM-API returns data as a JSON string — parse it
        Object dataObj = responseBody.get("data");
        JsonObject dataJson;
        if (dataObj instanceof String) {
            dataJson = JsonParser.parseString((String) dataObj).getAsJsonObject();
        } else {
            dataJson = JsonParser.parseString(new com.google.gson.Gson().toJson(dataObj)).getAsJsonObject();
        }
        Long benRegID = dataJson.get("benRegId").getAsLong();
        Long beneficiaryID = dataJson.get("benGenId").getAsLong();

        // Step 2 — save Stop TB specific fields @Transactional — all or nothing
        StopTBRegistration registration = new StopTBRegistration();
        registration.setBenRegID(benRegID);
        registration.setProviderServiceMapID(providerServiceMapID);
        registration.setPersonFrom(personFrom);
        registration.setCaseFindingType(caseFindingType);
        registration.setTuId(tuId);
        registration.setTuName(tuName);
        registration.setHealthFacilityId(healthFacilityId);
        registration.setHealthFacilityName(healthFacilityName);
        registration.setWeight(weight);
        registration.setHeight(height);
        registration.setBmi(bmi);
        registration.setTemperatureValue(temperatureValue);
        registration.setCreatedBy(createdBy);
        registration.setDeleted(false);
        stopTBRegistrationRepo.save(registration);

        // Step 3 — create flow record nurse_flag=1
        BenFlowStatus flowStatus = new BenFlowStatus();
        flowStatus.setBeneficiaryRegID(benRegID);
        flowStatus.setBeneficiaryID(beneficiaryID);
        flowStatus.setVisitCategory("Stop TB");
        flowStatus.setNurseFlag((short) 1);
        flowStatus.setDoctorFlag((short) 0);
        flowStatus.setPharmacistFlag((short) 0);
        flowStatus.setProviderServiceMapId(providerServiceMapID);
        flowStatus.setVanID(null);
        flowStatus.setAgentId(createdBy);
        flowStatus.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        benFlowStatusRepo.save(flowStatus);

        result.put("beneficiaryRegID", benRegID);
        result.put("beneficiaryID", beneficiaryID);
        return result;
    }

    @Override
    public Map<String, Object> getRegistration(StopTBRegistrationDto dto) throws Exception {
        Map<String, Object> result = new HashMap<>();
        StopTBRegistration registration = stopTBRegistrationRepo.findByBenRegID(dto.getBenRegID());
        if (registration == null)
            throw new Exception("No registration found for benRegID: " + dto.getBenRegID());

        result.put("benRegID", registration.getBenRegID());
        result.put("personFrom", registration.getPersonFrom());
        result.put("caseFindingType", registration.getCaseFindingType());
        result.put("tuId", registration.getTuId());
        result.put("tuName", registration.getTuName());
        result.put("healthFacilityId", registration.getHealthFacilityId());
        result.put("healthFacilityName", registration.getHealthFacilityName());
        result.put("weight", registration.getWeight());
        result.put("height", registration.getHeight());
        result.put("bmi", registration.getBmi());
        result.put("temperatureValue", registration.getTemperatureValue());
        result.put("createdBy", registration.getCreatedBy());
        result.put("createdDate", registration.getCreatedDate());
        return result;
    }

    @Override
    public Map<String, Object> getRegistrarWorklist(StopTBRegistrationDto dto) throws Exception {
        Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        Timestamp toDate = Timestamp.valueOf(LocalDate.now().atTime(LocalTime.MAX));
        List<StopTBRegistration> list = stopTBRegistrationRepo.getRegistrarWorklist(
                dto.getProviderServiceMapID(), dto.getCreatedBy(), fromDate, toDate);
        Map<String, Object> result = new HashMap<>();
        result.put("worklist", list);
        result.put("count", list.size());
        return result;
    }

    @Override
    public Map<String, Object> getNurseWorklist(StopTBRegistrationDto dto) throws Exception {
        Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        Timestamp toDate = Timestamp.valueOf(LocalDate.now().atTime(LocalTime.MAX));
        List<BenFlowStatus> list = benFlowStatusRepo.getNurseWorklist(
                dto.getProviderServiceMapID(), fromDate, toDate);
        Map<String, Object> result = new HashMap<>();
        result.put("worklist", list);
        result.put("count", list.size());
        return result;
    }

    private String getStringField(JsonObject obj, String field) {
        return obj.has(field) && !obj.get(field).isJsonNull() ? obj.get(field).getAsString() : null;
    }

    private Integer getIntField(JsonObject obj, String field) {
        if (!obj.has(field) || obj.get(field).isJsonNull()) return null;
        try {
            return obj.get(field).getAsInt();
        } catch (Exception e) {
            // handle string values like "1727"
            return Integer.parseInt(obj.get(field).getAsString().trim());
        }
    }

    private Double getDoubleField(JsonObject obj, String field) {
        return obj.has(field) && !obj.get(field).isJsonNull() ? obj.get(field).getAsDouble() : null;
    }
}
