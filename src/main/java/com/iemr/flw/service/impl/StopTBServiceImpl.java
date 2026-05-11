package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarymapping;
import com.iemr.flw.domain.iemr.BenFlowStatus;
import com.iemr.flw.domain.iemr.StopTBGeneralExamination;
import com.iemr.flw.domain.iemr.StopTBGeneralOpd;
import com.iemr.flw.domain.iemr.TBScreening;
import com.iemr.flw.dto.iemr.StopTBRegistrationDto;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.BenFlowStatusRepo;
import com.iemr.flw.repo.iemr.StopTBGeneralExaminationRepo;
import com.iemr.flw.repo.iemr.StopTBGeneralOpdRepo;
import com.iemr.flw.repo.iemr.TBScreeningRepo;
import com.iemr.flw.service.StopTBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

@Service
public class StopTBServiceImpl implements StopTBService {

    private final Logger logger = LoggerFactory.getLogger(StopTBServiceImpl.class);

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

    // ── Registration ──────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Map<String, Object> saveRegistration(String requestBody, String authorization) throws Exception {
        JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

        // Extract fields for BenFlowStatus — do NOT remove from jsonObject
        // All fields (including Stop TB extras) forward to TM-API → ExtraFields via checkExtraFields
        String firstName  = getStringField(jsonObject, "firstName");
        String lastName   = getStringField(jsonObject, "lastName");
        String benName    = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();

        Integer genderID  = getIntField(jsonObject, "genderID");
        String genderName = getStringField(jsonObject, "genderName");
        if (genderName == null) genderName = getStringField(jsonObject, "gender");

        String  createdBy          = getStringField(jsonObject, "createdBy");
        Integer providerServiceMapID = getIntField(jsonObject, "providerServiceMapID");

        Timestamp dob = parseDob(jsonObject);

        String phoneNo = null;
        if (jsonObject.has("i_bencontacts") && !jsonObject.get("i_bencontacts").isJsonNull()) {
            JsonObject contacts = jsonObject.getAsJsonObject("i_bencontacts");
            phoneNo = getStringField(contacts, "phoneNo1");
        }
        if (phoneNo == null) phoneNo = getStringField(jsonObject, "phoneNo1");
        if (phoneNo == null && jsonObject.has("benPhoneMaps") && jsonObject.get("benPhoneMaps").isJsonArray()) {
            com.google.gson.JsonArray phoneMaps = jsonObject.getAsJsonArray("benPhoneMaps");
            if (phoneMaps.size() > 0 && !phoneMaps.get(0).isJsonNull())
                phoneNo = getStringField(phoneMaps.get(0).getAsJsonObject(), "phoneNo");
        }

        Integer villageId   = null;
        String  villageName = null;
        Integer districtID  = null;
        String  districtName = null;
        if (jsonObject.has("i_bendemographics") && !jsonObject.get("i_bendemographics").isJsonNull()) {
            JsonObject demo = jsonObject.getAsJsonObject("i_bendemographics");
            villageId    = getIntField(demo, "districtBranchID");
            villageName  = getStringField(demo, "districtBranchName");
            districtID   = getIntField(demo, "districtID");
            districtName = getStringField(demo, "districtName");
            // use tb-prefixed keys so Identity-API treats as unknown and saves in ExtraFields
            if (demo.has("economicStatus"))    jsonObject.addProperty("tbEconomicStatus", getStringField(demo, "economicStatus"));
            if (demo.has("economicStatusId"))  jsonObject.addProperty("tbEconomicStatusId", getIntField(demo, "economicStatusId"));
            if (demo.has("residentialArea"))   jsonObject.addProperty("tbResidentialArea", getStringField(demo, "residentialArea"));
            if (demo.has("residentialAreaId")) jsonObject.addProperty("tbResidentialAreaId", getIntField(demo, "residentialAreaId"));
        }

        // isMobile=true tells TM-API to skip its own BenFlowStatus creation — FLW-API owns it
        jsonObject.addProperty("isMobile", true);

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
        JsonObject dataJson = dataObj instanceof String
                ? JsonParser.parseString((String) dataObj).getAsJsonObject()
                : JsonParser.parseString(new com.google.gson.Gson().toJson(dataObj)).getAsJsonObject();

        Long benRegID       = dataJson.get("benRegId").getAsLong();
        Long beneficiaryID  = dataJson.get("benGenId").getAsLong();

        BenFlowStatus flow = new BenFlowStatus();
        flow.setBeneficiaryRegID(benRegID);
        flow.setBeneficiaryID(beneficiaryID);
        flow.setBenName(benName.isEmpty() ? null : benName);
        flow.setDob(dob);
        flow.setGenderID(genderID != null ? genderID.shortValue() : null);
        flow.setGenderName(genderName);
        flow.setPreferredPhoneNum(phoneNo);
        flow.setNurseFlag((short) 1);
        flow.setDoctorFlag((short) 0);
        flow.setPharmacistFlag((short) 0);
        flow.setProviderServiceMapId(providerServiceMapID);
        flow.setVillageID(villageId);
        flow.setVillageName(villageName);
        flow.setDistrictID(districtID);
        flow.setDistrictName(districtName);
        flow.setAgentId(createdBy);
        flow.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        flow.setDeleted(false);
        benFlowStatusRepo.save(flow);

        Map<String, Object> result = new HashMap<>();
        result.put("beneficiaryRegID", benRegID);
        result.put("beneficiaryID", beneficiaryID);
        return result;
    }

    // ── Worklists ─────────────────────────────────────────────────────────────

    @Override
    public Map<String, Object> getRegistrarWorklist(StopTBRegistrationDto dto) throws Exception {
        List<BenFlowStatus> flowList = benFlowStatusRepo.getRegistrarWorklist(
                dto.getProviderServiceMapID(), dto.getVillageId());

        List<Map<String, Object>> worklist = new ArrayList<>();
        for (BenFlowStatus flow : flowList)
            worklist.add(buildWorklistItem(flow));

        return wrapWorklist(worklist);
    }

    @Override
    public Map<String, Object> getNurseWorklist(StopTBRegistrationDto dto) throws Exception {
        List<BenFlowStatus> flowList = benFlowStatusRepo.getNurseWorklist(
                dto.getProviderServiceMapID(), dto.getVillageId());

        List<Map<String, Object>> worklist = new ArrayList<>();
        for (BenFlowStatus flow : flowList) {
            Map<String, Object> item = buildWorklistItem(flow);
            Long benRegID = flow.getBeneficiaryRegID();

            StopTBGeneralExamination exam = generalExaminationRepo.findByBeneficiaryRegID(benRegID);
            item.put("generalExamination", exam != null ? examToMap(exam) : null);

            TBScreening screening = tbScreeningRepo.findByBenRegID(benRegID);
            item.put("tbScreening", screening != null ? screeningToMap(screening) : null);

            StopTBGeneralOpd opd = generalOpdRepo.findByBenRegID(benRegID);
            item.put("generalOpd", opd != null ? opdToMap(opd) : null);

            worklist.add(item);
        }

        return wrapWorklist(worklist);
    }

    // getBeneficiaryData-style item — BenFlowStatus + db_identity detail + ExtraFields
    private Map<String, Object> buildWorklistItem(BenFlowStatus flow) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("benficieryid", flow.getBeneficiaryID());
        item.put("BenRegId",     flow.getBeneficiaryRegID());
        item.put("benFlowID",    flow.getBenFlowID());
        item.put("nurseFlag",    flow.getNurseFlag());
        item.put("doctorFlag",   flow.getDoctorFlag());

        Map<String, Object> benDetails = new LinkedHashMap<>();
        benDetails.put("benName",          flow.getBenName());
        benDetails.put("dob",              flow.getDob());
        benDetails.put("genderId",         flow.getGenderID());
        benDetails.put("gender",           flow.getGenderName());
        benDetails.put("contact_number",   flow.getPreferredPhoneNum());
        benDetails.put("villageId",        flow.getVillageID());
        benDetails.put("villageName",      flow.getVillageName());
        benDetails.put("districtid",       flow.getDistrictID());
        benDetails.put("districtname",     flow.getDistrictName());
        benDetails.put("registrationDate", flow.getRegistrationDate());

        try {
            RMNCHMBeneficiarymapping mapping = beneficiaryRepo.getById(
                    BigInteger.valueOf(flow.getBeneficiaryRegID()));
            RMNCHMBeneficiarydetail detail = (mapping != null && mapping.getBenDetailsId() != null)
                    ? beneficiaryRepo.getDetailsById(mapping.getBenDetailsId())
                    : null;
            if (detail != null) {
                benDetails.put("fatherName",    detail.getFatherName());
                benDetails.put("motherName",    detail.getMotherName());
                benDetails.put("community",     detail.getCommunity());
                benDetails.put("communityId",   detail.getCommunityId());
                benDetails.put("education",     detail.getEducation());
                benDetails.put("maritalStatus", detail.getMaritalstatus());

                if (detail.getOtherFields() != null && !detail.getOtherFields().isEmpty()) {
                    try {
                        Map<String, Object> extra = new ObjectMapper().readValue(detail.getOtherFields(), Map.class);

                        Map<String, Object> anthropometry = new LinkedHashMap<>();
                        anthropometry.put("weight",           extra.get("weight"));
                        anthropometry.put("height",           extra.get("height"));
                        anthropometry.put("bmi",              extra.get("bmi"));
                        anthropometry.put("temperatureValue", extra.get("temperatureValue"));
                        item.put("anthropometry", anthropometry);

                        Map<String, Object> stopTBDetails = new LinkedHashMap<>();
                        stopTBDetails.put("personFrom",         extra.get("personFrom"));
                        stopTBDetails.put("caseFindingType",    extra.get("caseFindingType"));
                        stopTBDetails.put("tuId",               extra.get("tuId"));
                        stopTBDetails.put("tuName",             extra.get("tuName"));
                        stopTBDetails.put("healthFacilityId",   extra.get("healthFacilityId"));
                        stopTBDetails.put("healthFacilityName", extra.get("healthFacilityName"));
                        item.put("stopTBDetails", stopTBDetails);
                    } catch (Exception e) {
                        logger.warn("Cannot parse otherFields for benRegID: " + flow.getBeneficiaryRegID());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Cannot fetch identity detail for benRegID: " + flow.getBeneficiaryRegID() + " — " + e.getClass().getName() + ": " + e.getMessage());
        }

        item.put("beneficiaryDetails", benDetails);
        return item;
    }

    private Map<String, Object> wrapWorklist(List<Map<String, Object>> worklist) {
        Map<String, Object> inner = new LinkedHashMap<>();
        inner.put("totalPage", 1);
        inner.put("data", worklist);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", inner);
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
        exam.setPulseRate(getInt(data, "pulseRate"));
        exam.setSystolicBP(getInt(data, "systolicBP"));
        exam.setDiastolicBP(getInt(data, "diastolicBP"));
        exam.setRandomBloodSugar(getDouble(data, "randomBloodSugar"));
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
        List<Map<String, Object>> items = new ArrayList<>();
        for (StopTBGeneralExamination e : list) items.add(examToMap(e));
        Map<String, Object> result = new HashMap<>();
        result.put("data", items);
        result.put("count", items.size());
        return result;
    }

    private Map<String, Object> examToMap(StopTBGeneralExamination e) {
        Map<String, Object> m = new LinkedHashMap<>();
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
        List<Map<String, Object>> items = new ArrayList<>();
        for (TBScreening s : list) items.add(screeningToMap(s));
        Map<String, Object> result = new HashMap<>();
        result.put("data", items);
        result.put("count", items.size());
        return result;
    }

    private Map<String, Object> screeningToMap(TBScreening s) {
        Map<String, Object> m = new LinkedHashMap<>();
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

    // ── Nurse: General OPD ────────────────────────────────────────────────────

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
        List<Map<String, Object>> items = new ArrayList<>();
        for (StopTBGeneralOpd o : list) items.add(opdToMap(o));
        Map<String, Object> result = new HashMap<>();
        result.put("data", items);
        result.put("count", items.size());
        return result;
    }

    private Map<String, Object> opdToMap(StopTBGeneralOpd o) {
        Map<String, Object> m = new LinkedHashMap<>();
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

    private Timestamp parseDob(JsonObject obj) {
        if (!obj.has("dob") || obj.get("dob").isJsonNull()) return null;
        try { return new Timestamp(obj.get("dob").getAsLong()); } catch (Exception ignored) {}
        try { return Timestamp.valueOf(obj.get("dob").getAsString().replace("T", " ")); } catch (Exception ignored) {}
        return null;
    }

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
        try { return obj.get(field).getAsInt(); } catch (Exception e) {
            try { return Integer.parseInt(obj.get(field).getAsString().trim()); } catch (Exception ex) { return null; }
        }
    }
}
