package com.iemr.flw.service.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.iemr.BenFlowStatus;
import com.iemr.flw.domain.iemr.StopTBGeneralExamination;
import com.iemr.flw.domain.iemr.StopTBRegistration;
import com.iemr.flw.domain.iemr.TBScreening;
import com.iemr.flw.dto.iemr.StopTBRegistrationDto;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.BenFlowStatusRepo;
import com.iemr.flw.domain.iemr.StopTBGeneralOpd;
import com.iemr.flw.repo.iemr.StopTBGeneralExaminationRepo;
import com.iemr.flw.repo.iemr.StopTBGeneralOpdRepo;
import com.iemr.flw.repo.iemr.StopTBRegistrationRepo;
import com.iemr.flw.repo.iemr.TBScreeningRepo;
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

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private StopTBGeneralExaminationRepo generalExaminationRepo;

    @Autowired
    private TBScreeningRepo tbScreeningRepo;

    @Autowired
    private StopTBGeneralOpdRepo generalOpdRepo;

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

        // Stop TB specific fields
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

        // Beneficiary profile from db_identity
        RMNCHMBeneficiarydetail detail = beneficiaryRepo.getDetailByBenRegID(
                java.math.BigInteger.valueOf(dto.getBenRegID()));
        if (detail != null) {
            result.put("firstName", detail.getFirstName());
            result.put("middleName", detail.getMiddleName());
            result.put("lastName", detail.getLastName());
            result.put("dob", detail.getDob());
            result.put("gender", detail.getGender());
            result.put("fatherName", detail.getFatherName());
            result.put("motherName", detail.getMotherName());
            result.put("community", detail.getCommunity());
            result.put("education", detail.getEducation());
            result.put("maritalStatus", detail.getMaritalstatus());
        }
        return result;
    }

    @Override
    public Map<String, Object> getRegistrarWorklist(StopTBRegistrationDto dto) throws Exception {
        List<StopTBRegistration> list = stopTBRegistrationRepo.getRegistrarWorklist(
                dto.getProviderServiceMapID());

        List<Map<String, Object>> worklist = new java.util.ArrayList<>();
        for (StopTBRegistration reg : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("benRegID", reg.getBenRegID());
            item.put("providerServiceMapID", reg.getProviderServiceMapID());
            item.put("personFrom", reg.getPersonFrom());
            item.put("caseFindingType", reg.getCaseFindingType());
            item.put("tuId", reg.getTuId());
            item.put("tuName", reg.getTuName());
            item.put("healthFacilityId", reg.getHealthFacilityId());
            item.put("healthFacilityName", reg.getHealthFacilityName());
            item.put("weight", reg.getWeight());
            item.put("height", reg.getHeight());
            item.put("bmi", reg.getBmi());
            item.put("temperatureValue", reg.getTemperatureValue());
            item.put("createdBy", reg.getCreatedBy());
            item.put("createdDate", reg.getCreatedDate());

            RMNCHMBeneficiarydetail detail = beneficiaryRepo.getDetailByBenRegID(
                    java.math.BigInteger.valueOf(reg.getBenRegID()));
            if (detail != null) {
                item.put("firstName", detail.getFirstName());
                item.put("middleName", detail.getMiddleName());
                item.put("lastName", detail.getLastName());
                item.put("dob", detail.getDob());
                item.put("gender", detail.getGender());
                item.put("fatherName", detail.getFatherName());
                item.put("motherName", detail.getMotherName());
            }
            worklist.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("worklist", worklist);
        result.put("count", worklist.size());
        return result;
    }

    @Override
    public Map<String, Object> getNurseWorklist(StopTBRegistrationDto dto) throws Exception {
        List<BenFlowStatus> list = benFlowStatusRepo.getNurseWorklist(
                dto.getProviderServiceMapID());

        List<Map<String, Object>> worklist = new java.util.ArrayList<>();
        for (BenFlowStatus flow : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("benFlowID", flow.getBenFlowID());
            item.put("benRegID", flow.getBeneficiaryRegID());
            item.put("beneficiaryID", flow.getBeneficiaryID());
            item.put("visitCategory", flow.getVisitCategory());
            item.put("nurseFlag", flow.getNurseFlag());
            item.put("registrationDate", flow.getRegistrationDate());

            RMNCHMBeneficiarydetail detail = beneficiaryRepo.getDetailByBenRegID(
                    java.math.BigInteger.valueOf(flow.getBeneficiaryRegID()));
            if (detail != null) {
                item.put("firstName", detail.getFirstName());
                item.put("middleName", detail.getMiddleName());
                item.put("lastName", detail.getLastName());
                item.put("dob", detail.getDob());
                item.put("gender", detail.getGender());
                item.put("fatherName", detail.getFatherName());
                item.put("motherName", detail.getMotherName());
            }
            worklist.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("worklist", worklist);
        result.put("count", worklist.size());
        return result;
    }

    // ── Nurse: General Examination ────────────────────────────────────────────

    @Override
    @Transactional
    public Map<String, Object> saveGeneralExamination(Map<String, Object> data) throws Exception {
        Long benRegID = getLong(data, "benRegID");
        if (benRegID == null) throw new Exception("benRegID is required");

        StopTBGeneralExamination exam = generalExaminationRepo.findByBenRegID(benRegID);
        if (exam == null) exam = new StopTBGeneralExamination();

        exam.setBenRegID(benRegID);
        exam.setProviderServiceMapID(getInt(data, "providerServiceMapID"));
        exam.setPulse(getInt(data, "pulse"));
        exam.setSystolicBP(getInt(data, "systolicBP"));
        exam.setDiastolicBP(getInt(data, "diastolicBP"));
        exam.setRbsValue(getDouble(data, "rbsValue"));
        exam.setPallor(getBool(data, "pallor"));
        exam.setIcterus(getBool(data, "icterus"));
        exam.setLymphadenopathy(getBool(data, "lymphadenopathy"));
        exam.setEdema(getBool(data, "edema"));
        exam.setCyanosis(getBool(data, "cyanosis"));
        exam.setClubbing(getBool(data, "clubbing"));
        exam.setKeyPopulationRiskFactors(getString(data, "keyPopulationRiskFactors"));
        exam.setHivStatus(getString(data, "hivStatus"));
        exam.setCreatedBy(getString(data, "createdBy"));
        exam.setDeleted(false);
        exam.setReferralToHWCNeeded(calculateReferralNeeded(exam));

        generalExaminationRepo.save(exam);

        Map<String, Object> result = new HashMap<>();
        result.put("benRegID", benRegID);
        result.put("referralToHWCNeeded", exam.getReferralToHWCNeeded());
        return result;
    }

    private boolean calculateReferralNeeded(StopTBGeneralExamination e) {
        if (e.getPulse() != null && (e.getPulse() < 60 || e.getPulse() > 90)) return true;
        if (e.getSystolicBP() != null && (e.getSystolicBP() >= 140 || e.getSystolicBP() < 90)) return true;
        if (e.getDiastolicBP() != null && (e.getDiastolicBP() >= 90 || e.getDiastolicBP() < 60)) return true;
        if (e.getRbsValue() != null && e.getRbsValue() >= 100) return true;
        return false;
    }

    @Override
    public Map<String, Object> getGeneralExamination(Long benRegID) throws Exception {
        StopTBGeneralExamination exam = generalExaminationRepo.findByBenRegID(benRegID);
        if (exam == null) throw new Exception("No general examination found for benRegID: " + benRegID);
        return examToMap(exam);
    }

    @Override
    public Map<String, Object> getAllGeneralExaminations(Integer providerServiceMapID) throws Exception {
        List<StopTBGeneralExamination> list = generalExaminationRepo.findAllByProviderServiceMapID(providerServiceMapID);
        List<Map<String, Object>> items = new java.util.ArrayList<>();
        for (StopTBGeneralExamination e : list) items.add(examToMap(e));
        Map<String, Object> result = new HashMap<>();
        result.put("data", items);
        result.put("count", items.size());
        return result;
    }

    private Map<String, Object> examToMap(StopTBGeneralExamination e) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", e.getId());
        m.put("benRegID", e.getBenRegID());
        m.put("providerServiceMapID", e.getProviderServiceMapID());
        m.put("pulse", e.getPulse());
        m.put("systolicBP", e.getSystolicBP());
        m.put("diastolicBP", e.getDiastolicBP());
        m.put("rbsValue", e.getRbsValue());
        m.put("pallor", e.getPallor());
        m.put("icterus", e.getIcterus());
        m.put("lymphadenopathy", e.getLymphadenopathy());
        m.put("edema", e.getEdema());
        m.put("cyanosis", e.getCyanosis());
        m.put("clubbing", e.getClubbing());
        m.put("keyPopulationRiskFactors", e.getKeyPopulationRiskFactors());
        m.put("hivStatus", e.getHivStatus());
        m.put("referralToHWCNeeded", e.getReferralToHWCNeeded());
        m.put("createdBy", e.getCreatedBy());
        m.put("createdDate", e.getCreatedDate());
        return m;
    }

    // ── Nurse: TB Screening ───────────────────────────────────────────────────

    @Override
    @Transactional
    public Map<String, Object> saveNurseTBScreening(Map<String, Object> data) throws Exception {
        Long benRegID = getLong(data, "benRegID");
        if (benRegID == null) throw new Exception("benRegID is required");

        TBScreening screening = tbScreeningRepo.findByBenRegID(benRegID);
        if (screening == null) screening = new TBScreening();

        screening.setBenRegID(benRegID);
        screening.setProviderServiceMapID(getInt(data, "providerServiceMapID"));
        screening.setCoughMoreThan2Weeks(getBool(data, "coughMoreThan2Weeks"));
        screening.setBloodInSputum(getBool(data, "bloodInSputum"));
        screening.setFeverMoreThan2Weeks(getBool(data, "feverMoreThan2Weeks"));
        screening.setLossOfWeight(getBool(data, "lossOfWeight"));
        screening.setNightSweats(getBool(data, "nightSweats"));
        screening.setHistoryOfTb(getBool(data, "historyOfTb"));
        screening.setTakingAntiTBDrugs(getBool(data, "takingAntiTBDrugs"));
        screening.setFamilySufferingFromTB(getBool(data, "familySufferingFromTB"));
        screening.setRiseOfFever(getBool(data, "riseOfFever"));
        screening.setLossOfAppetite(getBool(data, "lossOfAppetite"));
        screening.setContactWithTBPatient(getBool(data, "contactWithTBPatient"));
        screening.setSympotomatic(getString(data, "symptomatic"));
        screening.setReferredForDigitalChestXray(getBool(data, "referredForDigitalChestXray"));
        screening.setReferredForSputumCollection(getBool(data, "referredForSputumCollection"));
        screening.setSputumSampleSubmittedAt(getString(data, "sputumSampleSubmittedAt"));
        screening.setRecommendedForTruenat(getBool(data, "recommendedForTruenat"));
        screening.setRecommendedForLiquidCulture(getBool(data, "recommendedForLiquidCulture"));
        screening.setTestDenialReasons(getString(data, "testDenialReasons"));
        screening.setCreatedBy(getString(data, "createdBy"));
        screening.setVisitDate(new Timestamp(System.currentTimeMillis()));

        tbScreeningRepo.save(screening);

        Map<String, Object> result = new HashMap<>();
        result.put("benRegID", benRegID);
        return result;
    }

    @Override
    public Map<String, Object> getNurseTBScreening(Long benRegID) throws Exception {
        TBScreening screening = tbScreeningRepo.findByBenRegID(benRegID);
        if (screening == null) throw new Exception("No TB screening found for benRegID: " + benRegID);
        return screeningToMap(screening);
    }

    @Override
    public Map<String, Object> getAllNurseTBScreenings(Integer providerServiceMapID) throws Exception {
        List<TBScreening> list = tbScreeningRepo.findAllByProviderServiceMapID(providerServiceMapID);
        List<Map<String, Object>> items = new java.util.ArrayList<>();
        for (TBScreening s : list) items.add(screeningToMap(s));
        Map<String, Object> result = new HashMap<>();
        result.put("data", items);
        result.put("count", items.size());
        return result;
    }

    private Map<String, Object> screeningToMap(TBScreening s) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", s.getId());
        m.put("benRegID", s.getBenRegID());
        m.put("providerServiceMapID", s.getProviderServiceMapID());
        m.put("coughMoreThan2Weeks", s.getCoughMoreThan2Weeks());
        m.put("bloodInSputum", s.getBloodInSputum());
        m.put("feverMoreThan2Weeks", s.getFeverMoreThan2Weeks());
        m.put("lossOfWeight", s.getLossOfWeight());
        m.put("nightSweats", s.getNightSweats());
        m.put("historyOfTb", s.getHistoryOfTb());
        m.put("takingAntiTBDrugs", s.getTakingAntiTBDrugs());
        m.put("familySufferingFromTB", s.getFamilySufferingFromTB());
        m.put("riseOfFever", s.getRiseOfFever());
        m.put("lossOfAppetite", s.getLossOfAppetite());
        m.put("contactWithTBPatient", s.getContactWithTBPatient());
        m.put("symptomatic", s.getSympotomatic());
        m.put("referredForDigitalChestXray", s.getReferredForDigitalChestXray());
        m.put("referredForSputumCollection", s.getReferredForSputumCollection());
        m.put("sputumSampleSubmittedAt", s.getSputumSampleSubmittedAt());
        m.put("recommendedForTruenat", s.getRecommendedForTruenat());
        m.put("recommendedForLiquidCulture", s.getRecommendedForLiquidCulture());
        m.put("testDenialReasons", s.getTestDenialReasons());
        m.put("createdBy", s.getCreatedBy());
        m.put("visitDate", s.getVisitDate());
        return m;
    }

    // ── Nurse: General OPD ───────────────────────────────────────────────────

    @Override
    @Transactional
    public Map<String, Object> saveGeneralOpd(Map<String, Object> data) throws Exception {
        Long benRegID = getLong(data, "benRegID");
        if (benRegID == null) throw new Exception("benRegID is required");

        StopTBGeneralOpd opd = generalOpdRepo.findByBenRegID(benRegID);
        if (opd == null) opd = new StopTBGeneralOpd();

        opd.setBenRegID(benRegID);
        opd.setProviderServiceMapID(getInt(data, "providerServiceMapID"));
        opd.setChiefComplaint(getString(data, "chiefComplaint"));
        opd.setMedication(getString(data, "medication"));
        opd.setDosage(getString(data, "dosage"));
        opd.setFrequency(getString(data, "frequency"));
        opd.setDuration(getString(data, "duration"));
        opd.setNotes(getString(data, "notes"));
        opd.setCreatedBy(getString(data, "createdBy"));
        opd.setDeleted(false);

        generalOpdRepo.save(opd);

        Map<String, Object> result = new HashMap<>();
        result.put("benRegID", benRegID);
        return result;
    }

    @Override
    public Map<String, Object> getGeneralOpd(Long benRegID) throws Exception {
        StopTBGeneralOpd opd = generalOpdRepo.findByBenRegID(benRegID);
        if (opd == null) throw new Exception("No general OPD record found for benRegID: " + benRegID);
        return opdToMap(opd);
    }

    @Override
    public Map<String, Object> getAllGeneralOpd(Integer providerServiceMapID) throws Exception {
        List<StopTBGeneralOpd> list = generalOpdRepo.findAllByProviderServiceMapID(providerServiceMapID);
        List<Map<String, Object>> items = new java.util.ArrayList<>();
        for (StopTBGeneralOpd o : list) items.add(opdToMap(o));
        Map<String, Object> result = new HashMap<>();
        result.put("data", items);
        result.put("count", items.size());
        return result;
    }

    private Map<String, Object> opdToMap(StopTBGeneralOpd o) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", o.getId());
        m.put("benRegID", o.getBenRegID());
        m.put("providerServiceMapID", o.getProviderServiceMapID());
        m.put("chiefComplaint", o.getChiefComplaint());
        m.put("medication", o.getMedication());
        m.put("dosage", o.getDosage());
        m.put("frequency", o.getFrequency());
        m.put("duration", o.getDuration());
        m.put("notes", o.getNotes());
        m.put("createdBy", o.getCreatedBy());
        m.put("createdDate", o.getCreatedDate());
        return m;
    }

    // ── Nurse: Submit ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Map<String, Object> submitNurseData(Map<String, Object> data) throws Exception {
        Long benFlowID = getLong(data, "benFlowID");
        if (benFlowID == null) throw new Exception("benFlowID is required");

        int updated = benFlowStatusRepo.updateStopTBAfterNurseSubmit(benFlowID);
        if (updated == 0) throw new Exception("Flow record not found for benFlowID: " + benFlowID);

        Map<String, Object> result = new HashMap<>();
        result.put("benFlowID", benFlowID);
        result.put("status", "nurse_done");
        return result;
    }

    // ── Map helper extractors ─────────────────────────────────────────────────

    private String getString(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v != null ? v.toString() : null;
    }

    private Long getLong(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(v.toString()); } catch (Exception e) { return null; }
    }

    private Integer getInt(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return null; }
    }

    private Double getDouble(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return null; }
    }

    private Boolean getBool(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return null;
        if (v instanceof Boolean) return (Boolean) v;
        return Boolean.parseBoolean(v.toString());
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
