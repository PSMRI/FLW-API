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

    // ── Registration ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Map<String, Object> saveRegistration(String requestBody, String authorization) throws Exception {
        Map<String, Object> result = new HashMap<>();

        JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

        // Stop TB specific fields
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
        // Village from demographics — same pattern as HWC getBenFlowRecordObj
        Integer villageId = null;
        String villageName = null;
        if (jsonObject.has("i_bendemographics") && !jsonObject.get("i_bendemographics").isJsonNull()) {
            JsonObject demo = jsonObject.getAsJsonObject("i_bendemographics");
            villageId = getIntField(demo, "districtBranchID");
            villageName = getStringField(demo, "districtBranchName");
        }

        // Strip Stop TB fields before forwarding to TM-API
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
        jsonObject.addProperty("isMobile", true);

        // Forward to TM-API for standard beneficiary registration
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorization);
        headers.set("User-Agent", "okhttp");

        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                tmUrl + "/registrar/registrarBeneficaryRegistrationNew",
                HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null)
            throw new Exception("Beneficiary registration failed in TM-API");

        Object dataObj = response.getBody().get("data");
        JsonObject dataJson;
        if (dataObj instanceof String) {
            dataJson = JsonParser.parseString((String) dataObj).getAsJsonObject();
        } else {
            dataJson = JsonParser.parseString(new com.google.gson.Gson().toJson(dataObj)).getAsJsonObject();
        }
        Long benRegID = dataJson.get("benRegId").getAsLong();
        Long beneficiaryID = dataJson.get("benGenId").getAsLong();

        // Save Stop TB registration
        StopTBRegistration registration = new StopTBRegistration();
        registration.setBenRegID(benRegID);
        registration.setProviderServiceMapID(providerServiceMapID);
        registration.setPersonFrom(personFrom);
        registration.setCaseFindingType(caseFindingType);
        registration.setTuId(tuId);
        registration.setTuName(tuName);
        registration.setHealthFacilityId(healthFacilityId);
        registration.setHealthFacilityName(healthFacilityName);
        registration.setVillageId(villageId);
        registration.setVillageName(villageName);
        registration.setWeight(weight);
        registration.setHeight(height);
        registration.setBmi(bmi);
        registration.setTemperatureValue(temperatureValue);
        registration.setCreatedBy(createdBy);
        registration.setDeleted(false);
        stopTBRegistrationRepo.save(registration);

        // Create flow record — nurse_flag=1, scoped by village (like HWC vanID)
        BenFlowStatus flowStatus = new BenFlowStatus();
        flowStatus.setBeneficiaryRegID(benRegID);
        flowStatus.setBeneficiaryID(beneficiaryID);
        flowStatus.setVisitCategory("Stop TB");
        flowStatus.setNurseFlag((short) 1);
        flowStatus.setDoctorFlag((short) 0);
        flowStatus.setPharmacistFlag((short) 0);
        flowStatus.setProviderServiceMapId(providerServiceMapID);
        flowStatus.setVillageID(villageId);
        flowStatus.setVillageName(villageName);
        flowStatus.setAgentId(createdBy);
        flowStatus.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        benFlowStatusRepo.save(flowStatus);

        result.put("beneficiaryRegID", benRegID);
        result.put("beneficiaryID", beneficiaryID);
        return result;
    }

    @Override
    public Map<String, Object> getRegistration(StopTBRegistrationDto dto) throws Exception {
        StopTBRegistration reg = stopTBRegistrationRepo.findByBenRegID(dto.getBenRegID());
        if (reg == null)
            throw new Exception("No registration found for benRegID: " + dto.getBenRegID());

        Map<String, Object> result = new HashMap<>();
        result.put("benRegID", reg.getBenRegID());
        result.put("personFrom", reg.getPersonFrom());
        result.put("caseFindingType", reg.getCaseFindingType());
        result.put("tuId", reg.getTuId());
        result.put("tuName", reg.getTuName());
        result.put("healthFacilityId", reg.getHealthFacilityId());
        result.put("healthFacilityName", reg.getHealthFacilityName());
        result.put("villageId", reg.getVillageId());
        result.put("villageName", reg.getVillageName());
        result.put("weight", reg.getWeight());
        result.put("height", reg.getHeight());
        result.put("bmi", reg.getBmi());
        result.put("temperatureValue", reg.getTemperatureValue());
        result.put("createdBy", reg.getCreatedBy());
        result.put("createdDate", reg.getCreatedDate());

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

    // ── Beneficiary Details ───────────────────────────────────────────────────

    @Override
    public Map<String, Object> getBeneficiaryDetails(Long beneficiaryRegID, String authorization) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("JwtToken", authorization);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("beneficiaryRegID", beneficiaryRegID);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                tmUrl + "/registrar/quickSearchNew",
                HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null)
            throw new Exception("Failed to fetch beneficiary details from TM-API");

        Map<String, Object> body = response.getBody();
        Object dataObj = body.get("data");
        if (dataObj == null)
            throw new Exception("No data in TM-API response");

        // quickSearchNew returns a list — take first element
        java.util.List<?> dataList = (java.util.List<?>) dataObj;
        if (dataList.isEmpty())
            throw new Exception("Beneficiary not found");

        Map<String, Object> benData = new HashMap<>((Map<String, Object>) dataList.get(0));

        // Parse otherFields JSON string and merge into response
        Object otherFieldsRaw = benData.get("otherFields");
        if (otherFieldsRaw != null) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Map<String, Object> otherFields = mapper.readValue(otherFieldsRaw.toString(), Map.class);
                benData.putAll(otherFields);
            } catch (Exception e) {
                logger.warn("Could not parse otherFields: " + e.getMessage());
            }
        }
        benData.remove("otherFields");
        return benData;
    }

    // ── Worklists ─────────────────────────────────────────────────────────────

    @Override
    public Map<String, Object> getRegistrarWorklist(StopTBRegistrationDto dto) throws Exception {
        List<BenFlowStatus> list = benFlowStatusRepo.getRegistrarWorklist(
                dto.getProviderServiceMapID(), dto.getVillageId());

        List<Map<String, Object>> worklist = new java.util.ArrayList<>();
        for (BenFlowStatus flow : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("benFlowID", flow.getBenFlowID());
            item.put("benRegID", flow.getBeneficiaryRegID());
            item.put("beneficiaryID", flow.getBeneficiaryID());
            item.put("benName", flow.getBenName());
            item.put("dob", flow.getDob());
            item.put("genderID", flow.getGenderID());
            item.put("genderName", flow.getGenderName());
            item.put("phoneNo", flow.getPreferredPhoneNum());
            item.put("villageID", flow.getVillageID());
            item.put("villageName", flow.getVillageName());
            item.put("districtID", flow.getDistrictID());
            item.put("districtName", flow.getDistrictName());
            item.put("registrationDate", flow.getRegistrationDate());
            item.put("nurseFlag", flow.getNurseFlag());
            item.put("doctorFlag", flow.getDoctorFlag());
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
                dto.getProviderServiceMapID(), dto.getVillageId());

        List<Map<String, Object>> worklist = new java.util.ArrayList<>();
        for (BenFlowStatus flow : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("benFlowID", flow.getBenFlowID());
            item.put("benRegID", flow.getBeneficiaryRegID());
            item.put("beneficiaryID", flow.getBeneficiaryID());
            item.put("benName", flow.getBenName());
            item.put("dob", flow.getDob());
            item.put("genderID", flow.getGenderID());
            item.put("genderName", flow.getGenderName());
            item.put("phoneNo", flow.getPreferredPhoneNum());
            item.put("districtID", flow.getDistrictID());
            item.put("districtName", flow.getDistrictName());
            item.put("villageID", flow.getVillageID());
            item.put("villageName", flow.getVillageName());
            item.put("providerServiceMapID", flow.getProviderServiceMapId());
            item.put("nurseFlag", flow.getNurseFlag());
            item.put("doctorFlag", flow.getDoctorFlag());
            item.put("registrationDate", flow.getRegistrationDate());
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
        Long beneficiaryRegID = getLong(data, "beneficiaryRegID");
        if (beneficiaryRegID == null) throw new Exception("beneficiaryRegID is required");

        StopTBGeneralExamination exam = generalExaminationRepo.findByBeneficiaryRegID(beneficiaryRegID);
        if (exam == null) exam = new StopTBGeneralExamination();

        exam.setBeneficiaryRegID(beneficiaryRegID);
        exam.setProviderServiceMapID(getInt(data, "providerServiceMapID"));

        // Vitals
        exam.setPulseRate(getInt(data, "pulseRate"));
        exam.setSystolicBP(getInt(data, "systolicBP"));
        exam.setDiastolicBP(getInt(data, "diastolicBP"));
        exam.setRandomBloodSugar(getDouble(data, "randomBloodSugar"));

        // Clinical signs — ID + label
        exam.setPallorId(getInt(data, "pallorId"));
        exam.setPallor(getString(data, "pallor"));
        exam.setIcterusId(getInt(data, "icterusId"));
        exam.setIcterus(getString(data, "icterus"));
        exam.setLymphadenopathyId(getInt(data, "lymphadenopathyId"));
        exam.setLymphadenopathy(getString(data, "lymphadenopathy"));
        exam.setOedemaId(getInt(data, "oedemaId"));
        exam.setOedema(getString(data, "oedema"));
        exam.setCyanosisId(getInt(data, "cyanosisId"));
        exam.setCyanosis(getString(data, "cyanosis"));
        exam.setClubbingId(getInt(data, "clubbingId"));
        exam.setClubbing(getString(data, "clubbing"));

        // JSON arrays from mobile
        exam.setKeyPopulationRiskFactorIds(toJsonString(data.get("keyPopulationRiskFactorIds")));
        exam.setKeyPopulationRiskFactors(toJsonString(data.get("keyPopulationRiskFactors")));

        exam.setHivStatusId(getInt(data, "hivStatusId"));
        exam.setHivStatus(getString(data, "hivStatus"));
        exam.setCreatedBy(getString(data, "createdBy"));
        exam.setDeleted(false);
        exam.setReferralToHWCNeeded(calculateReferralNeeded(exam));

        generalExaminationRepo.save(exam);

        Map<String, Object> result = new HashMap<>();
        result.put("beneficiaryRegID", beneficiaryRegID);
        result.put("referralToHWCNeeded", exam.getReferralToHWCNeeded());
        return result;
    }

    private boolean calculateReferralNeeded(StopTBGeneralExamination e) {
        if (e.getPulseRate() != null && (e.getPulseRate() < 60 || e.getPulseRate() > 90)) return true;
        if (e.getSystolicBP() != null && (e.getSystolicBP() >= 140 || e.getSystolicBP() < 90)) return true;
        if (e.getDiastolicBP() != null && (e.getDiastolicBP() >= 90 || e.getDiastolicBP() < 60)) return true;
        if (e.getRandomBloodSugar() != null && e.getRandomBloodSugar() >= 100) return true;
        return false;
    }

    @Override
    public Map<String, Object> getGeneralExamination(Long beneficiaryRegID) throws Exception {
        StopTBGeneralExamination exam = generalExaminationRepo.findByBeneficiaryRegID(beneficiaryRegID);
        if (exam == null) throw new Exception("No general examination found for beneficiaryRegID: " + beneficiaryRegID);
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
        m.put("beneficiaryRegID", e.getBeneficiaryRegID());
        m.put("providerServiceMapID", e.getProviderServiceMapID());
        m.put("pulseRate", e.getPulseRate());
        m.put("systolicBP", e.getSystolicBP());
        m.put("diastolicBP", e.getDiastolicBP());
        m.put("randomBloodSugar", e.getRandomBloodSugar());
        m.put("pallorId", e.getPallorId());
        m.put("pallor", e.getPallor());
        m.put("icterusId", e.getIcterusId());
        m.put("icterus", e.getIcterus());
        m.put("lymphadenopathyId", e.getLymphadenopathyId());
        m.put("lymphadenopathy", e.getLymphadenopathy());
        m.put("oedemaId", e.getOedemaId());
        m.put("oedema", e.getOedema());
        m.put("cyanosisId", e.getCyanosisId());
        m.put("cyanosis", e.getCyanosis());
        m.put("clubbingId", e.getClubbingId());
        m.put("clubbing", e.getClubbing());
        m.put("keyPopulationRiskFactorIds", e.getKeyPopulationRiskFactorIds());
        m.put("keyPopulationRiskFactors", e.getKeyPopulationRiskFactors());
        m.put("hivStatusId", e.getHivStatusId());
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
        Long beneficiaryRegID = getLong(data, "beneficiaryRegID");
        if (beneficiaryRegID == null) throw new Exception("beneficiaryRegID is required");

        TBScreening screening = tbScreeningRepo.findByBenRegID(beneficiaryRegID);
        if (screening == null) screening = new TBScreening();

        screening.setBenRegID(beneficiaryRegID);
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
        result.put("beneficiaryRegID", beneficiaryRegID);
        return result;
    }

    @Override
    public Map<String, Object> getNurseTBScreening(Long beneficiaryRegID) throws Exception {
        TBScreening screening = tbScreeningRepo.findByBenRegID(beneficiaryRegID);
        if (screening == null) throw new Exception("No TB screening found for beneficiaryRegID: " + beneficiaryRegID);
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
        m.put("beneficiaryRegID", s.getBenRegID());
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
        Long beneficiaryRegID = getLong(data, "beneficiaryRegID");
        if (beneficiaryRegID == null) throw new Exception("beneficiaryRegID is required");

        StopTBGeneralOpd opd = generalOpdRepo.findByBenRegID(beneficiaryRegID);
        if (opd == null) opd = new StopTBGeneralOpd();

        opd.setBenRegID(beneficiaryRegID);
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
        result.put("beneficiaryRegID", beneficiaryRegID);
        return result;
    }

    @Override
    public Map<String, Object> getGeneralOpd(Long beneficiaryRegID) throws Exception {
        StopTBGeneralOpd opd = generalOpdRepo.findByBenRegID(beneficiaryRegID);
        if (opd == null) throw new Exception("No general OPD record found for beneficiaryRegID: " + beneficiaryRegID);
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
        m.put("beneficiaryRegID", o.getBenRegID());
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String toJsonString(Object value) {
        if (value == null) return null;
        if (value instanceof String) return (String) value;
        return new com.google.gson.Gson().toJson(value);
    }

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
            try { return Integer.parseInt(obj.get(field).getAsString().trim()); } catch (Exception ex) { return null; }
        }
    }

    private Double getDoubleField(JsonObject obj, String field) {
        return obj.has(field) && !obj.get(field).isJsonNull() ? obj.get(field).getAsDouble() : null;
    }
}
