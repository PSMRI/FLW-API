package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarymapping;
import com.iemr.flw.domain.iemr.BenAnthropometryDetail;
import com.iemr.flw.domain.iemr.BenChiefComplaint;
import com.iemr.flw.domain.iemr.BenFlowStatus;
import com.iemr.flw.domain.iemr.BenPhysicalVitalDetail;
import com.iemr.flw.domain.iemr.BenReferDetails;
import com.iemr.flw.domain.iemr.BenVisitDetail;
import com.iemr.flw.domain.iemr.PhyGeneralExamination;
import com.iemr.flw.domain.iemr.PrescribedDrugDetail;
import com.iemr.flw.domain.iemr.PrescriptionDetail;
import com.iemr.flw.domain.iemr.StopTBDiagnostics;
import com.iemr.flw.domain.iemr.StopTBGeneralExamination;
import com.iemr.flw.domain.iemr.StopTBGeneralOpd;
import com.iemr.flw.domain.iemr.TBScreening;
import com.iemr.flw.dto.iemr.StopTBRegistrationDto;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.BenAnthropometryRepo;
import com.iemr.flw.repo.iemr.BenChiefComplaintRepo;
import com.iemr.flw.repo.iemr.BenFlowStatusRepo;
import com.iemr.flw.repo.iemr.BenPhysicalVitalRepo;
import com.iemr.flw.repo.iemr.BenReferDetailsRepo;
import com.iemr.flw.repo.iemr.PhyGeneralExaminationRepo;
import com.iemr.flw.repo.iemr.PrescribedDrugDetailRepo;
import com.iemr.flw.repo.iemr.PrescriptionDetailRepo;
import com.iemr.flw.repo.iemr.StopTBDiagnosticsRepo;
import com.iemr.flw.repo.iemr.StopTBGeneralExaminationRepo;
import com.iemr.flw.repo.iemr.StopTBGeneralOpdRepo;
import com.iemr.flw.repo.iemr.TBScreeningRepo;
import com.iemr.flw.service.CampConfigService;
import com.iemr.flw.service.StopTBService;
import com.iemr.flw.service.TBStopVisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
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
    private CampConfigService campConfigService;

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

    @Autowired
    private StopTBDiagnosticsRepo diagnosticsRepo;

    @Autowired
    private TBStopVisitService tbStopVisitService;

    @Autowired
    private BenAnthropometryRepo benAnthropometryRepo;

    @Autowired
    private BenPhysicalVitalRepo benPhysicalVitalRepo;

    @Autowired
    private PhyGeneralExaminationRepo phyGeneralExaminationRepo;

    @Autowired
    private BenChiefComplaintRepo benChiefComplaintRepo;

    @Autowired
    private PrescriptionDetailRepo prescriptionDetailRepo;

    @Autowired
    private PrescribedDrugDetailRepo prescribedDrugDetailRepo;

    @Autowired
    private BenReferDetailsRepo benReferDetailsRepo;

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

            List<StopTBGeneralExamination> examList = generalExaminationRepo.findLatestByBeneficiaryRegID(benRegID, PageRequest.of(0, 1));
            item.put("generalExamination", !examList.isEmpty() ? examToMap(examList.get(0)) : null);

            List<TBScreening> screeningList = tbScreeningRepo.findLatestByBenRegID(benRegID, PageRequest.of(0, 1));
            item.put("tbScreening", !screeningList.isEmpty() ? screeningToMap(screeningList.get(0)) : null);

            List<StopTBGeneralOpd> opdList = generalOpdRepo.findLatestByBenRegID(benRegID, PageRequest.of(0, 1));
            item.put("generalOpd", !opdList.isEmpty() ? opdToMap(opdList.get(0)) : null);

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
    public List<Map<String, Object>> saveGeneralExamination(List<Map<String, Object>> dataList) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();
        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();
        for (Map<String, Object> data : dataList) {
            Long beneficiaryRegID = getLong(data, "beneficiaryRegID");
            if (beneficiaryRegID == null) throw new Exception("beneficiaryRegID is required");

            Integer providerServiceMapID = getInt(data, "providerServiceMapID");
            String createdBy = getString(data, "createdBy");
            BenVisitDetail visit = tbStopVisitService.getOrCreateVisitForToday(beneficiaryRegID, providerServiceMapID,
                    createdBy, vanID, parkingPlaceID);

            StopTBGeneralExamination exam = generalExaminationRepo.findByBeneficiaryRegIDAndVisitCode(beneficiaryRegID, visit.getVisitCode());
            if (exam == null) exam = new StopTBGeneralExamination();
            boolean isNew = exam.getId() == null;

            exam.setBeneficiaryRegID(beneficiaryRegID);
            exam.setVisitCode(visit.getVisitCode());
            exam.setBenVisitID(visit.getBenVisitId());
            exam.setProviderServiceMapID(providerServiceMapID);
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
            exam.setModifiedBy(getString(data, "createdBy"));
            exam.setDeleted(false);
            exam.setReferralToHWCNeeded(getBool(data, "referralToHWCNeeded"));
            if (exam.getVanID() == null && vanID != null) { exam.setVanID(vanID); exam.setParkingPlaceID(parkingPlaceID); }
            exam.setProcessed("N");

            generalExaminationRepo.save(exam);
            if (isNew) {
                generalExaminationRepo.updateVanSerialNo(exam.getId());
                dualWriteExamToStandardTables(exam, beneficiaryRegID, visit, createdBy, vanID, parkingPlaceID);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("beneficiaryRegID", beneficiaryRegID);
            result.put("referralToHWCNeeded", exam.getReferralToHWCNeeded());
            results.add(result);
        }
        return results;
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
        List<StopTBGeneralExamination> examList = generalExaminationRepo.findLatestByBeneficiaryRegID(beneficiaryRegID, PageRequest.of(0, 1));
        if (examList.isEmpty()) throw new Exception("No general examination found for beneficiaryRegID: " + beneficiaryRegID);
        return examToMap(examList.get(0));
    }

    @Override
    public Map<String, Object> getAllGeneralExaminations(Integer providerServiceMapID, Integer villageID) throws Exception {
        List<StopTBGeneralExamination> list = (villageID != null)
                ? generalExaminationRepo.findAllByProviderServiceMapIDAndVillageID(providerServiceMapID, villageID)
                : generalExaminationRepo.findAllByProviderServiceMapID(providerServiceMapID);
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
        m.put("updateDate", e.getLastModDate());
        m.put("updatedBy", e.getModifiedBy());
        return m;
    }

    // ── Nurse: TB Screening ───────────────────────────────────────────────────

    @Override
    @Transactional
    public List<Map<String, Object>> saveNurseTBScreening(List<Map<String, Object>> dataList) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();
        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();
        for (Map<String, Object> data : dataList) {
            Long beneficiaryRegID = getLong(data, "beneficiaryRegID");
            if (beneficiaryRegID == null) throw new Exception("beneficiaryRegID is required");

            Integer providerServiceMapID = getInt(data, "providerServiceMapID");
            String createdBy = getString(data, "createdBy");
            BenVisitDetail visit = tbStopVisitService.getOrCreateVisitForToday(beneficiaryRegID, providerServiceMapID,
                    createdBy, vanID, parkingPlaceID);

            TBScreening screening = tbScreeningRepo.findByBenRegIDAndVisitCode(beneficiaryRegID, visit.getVisitCode());
            if (screening == null) screening = new TBScreening();
            boolean isNew = screening.getId() == null;

            screening.setBenRegID(beneficiaryRegID);
            screening.setVisitCode(visit.getVisitCode());
            screening.setProviderServiceMapID(providerServiceMapID);
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
            screening.setKeyPopulationRiskFactorIds(toJsonString(data.get("keyPopulationRiskFactorIds")));
            screening.setKeyPopulationRiskFactors(toJsonString(data.get("keyPopulationRiskFactors")));
            screening.setHivStatusId(getInt(data, "hivStatusId"));
            screening.setHivStatus(getString(data, "hivStatus"));
            String symptomaticInput = getString(data, "symptomatic");
            if ("Yes".equalsIgnoreCase(symptomaticInput)) {
                screening.setSympotomatic(null);
                screening.setAsymptomatic("Yes");
            } else if ("No".equalsIgnoreCase(symptomaticInput)) {
                screening.setSympotomatic("Yes");
                screening.setAsymptomatic(null);
            } else {
                screening.setSympotomatic(null);
                screening.setAsymptomatic(null);
            }
            screening.setReferredForDigitalChestXray(getBool(data, "referredForDigitalChestXray"));
            screening.setReferredForSputumCollection(getBool(data, "referredForSputumCollection"));
            screening.setSputumSampleSubmittedAt(getString(data, "sputumSampleSubmittedAt"));
            screening.setRecommendedForTruenat(getBool(data, "recommendedForTruenat"));
            screening.setRecommendedForLiquidCulture(getBool(data, "recommendedForLiquidCulture"));
            screening.setTestDenialReasons(getString(data, "testDenialReasons"));
            screening.setCreatedBy(getString(data, "createdBy"));
            screening.setModifiedBy(getString(data, "createdBy"));
            // PRD: date is user-provided, mandatory, not editable once submitted
            if (screening.getVisitDate() == null) {
                Timestamp provided = getTimestamp(data, "visitDate");
                screening.setVisitDate(provided != null ? provided : new Timestamp(System.currentTimeMillis()));
            }
            if (screening.getVanID() == null && vanID != null) { screening.setVanID(vanID); screening.setParkingPlaceID(parkingPlaceID); }
            screening.setProcessed("N");

            tbScreeningRepo.save(screening);
            if (isNew) tbScreeningRepo.updateVanSerialNo(screening.getId());

            Map<String, Object> result = new HashMap<>();
            result.put("beneficiaryRegID", beneficiaryRegID);
            results.add(result);
        }
        return results;
    }

    @Override
    public Map<String, Object> getNurseTBScreening(Long beneficiaryRegID) throws Exception {
        List<TBScreening> screeningList = tbScreeningRepo.findLatestByBenRegID(beneficiaryRegID, PageRequest.of(0, 1));
        if (screeningList.isEmpty()) throw new Exception("No TB screening found for beneficiaryRegID: " + beneficiaryRegID);
        return screeningToMap(screeningList.get(0));
    }

    @Override
    public Map<String, Object> getAllNurseTBScreenings(Integer providerServiceMapID, Integer villageID) throws Exception {
        List<TBScreening> list = (villageID != null)
                ? tbScreeningRepo.findAllByProviderServiceMapIDAndVillageID(providerServiceMapID, villageID)
                : tbScreeningRepo.findAllByProviderServiceMapID(providerServiceMapID);
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
        m.put("keyPopulationRiskFactorIds", s.getKeyPopulationRiskFactorIds());
        m.put("keyPopulationRiskFactors", s.getKeyPopulationRiskFactors());
        m.put("hivStatusId", s.getHivStatusId());
        m.put("hivStatus", s.getHivStatus());
        if ("Yes".equalsIgnoreCase(s.getAsymptomatic())) {
            m.put("symptomatic", "Yes");
        } else if ("Yes".equalsIgnoreCase(s.getSympotomatic())) {
            m.put("symptomatic", "No");
        } else {
            m.put("symptomatic", null);
        }
        m.put("referredForDigitalChestXray", s.getReferredForDigitalChestXray());
        m.put("referredForSputumCollection", s.getReferredForSputumCollection());
        m.put("sputumSampleSubmittedAt", s.getSputumSampleSubmittedAt());
        m.put("recommendedForTruenat", s.getRecommendedForTruenat());
        m.put("recommendedForLiquidCulture", s.getRecommendedForLiquidCulture());
        m.put("testDenialReasons", s.getTestDenialReasons());
        m.put("createdBy", s.getCreatedBy());
        m.put("visitDate", s.getVisitDate());
        m.put("updateDate", s.getLastModDate());
        m.put("updatedBy", s.getModifiedBy());
        return m;
    }

    // ── Nurse: General OPD ────────────────────────────────────────────────────

    @Override
    @Transactional
    public List<Map<String, Object>> saveGeneralOpd(List<Map<String, Object>> dataList) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();
        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();
        for (Map<String, Object> data : dataList) {
            Long beneficiaryRegID = getLong(data, "beneficiaryRegID");
            if (beneficiaryRegID == null) throw new Exception("beneficiaryRegID is required");

            Integer providerServiceMapID = getInt(data, "providerServiceMapID");
            String createdBy = getString(data, "createdBy");
            BenVisitDetail visit = tbStopVisitService.getOrCreateVisitForToday(beneficiaryRegID, providerServiceMapID,
                    createdBy, vanID, parkingPlaceID);

            StopTBGeneralOpd opd = generalOpdRepo.findByBenRegIDAndVisitCode(beneficiaryRegID, visit.getVisitCode());
            if (opd == null) opd = new StopTBGeneralOpd();
            boolean isNew = opd.getId() == null;

            opd.setBenRegID(beneficiaryRegID);
            opd.setVisitCode(visit.getVisitCode());
            opd.setBenVisitID(visit.getBenVisitId());
            opd.setProviderServiceMapID(providerServiceMapID);
            opd.setChiefComplaint(toJsonString(data.get("chiefComplaint")));
            opd.setMedication(getString(data, "medication"));
            opd.setDosage(getString(data, "dosage"));
            opd.setFrequency(getString(data, "frequency"));
            opd.setDuration(getString(data, "duration"));
            opd.setNotes(getString(data, "notes"));
            opd.setCreatedBy(getString(data, "createdBy"));
            opd.setModifiedBy(getString(data, "createdBy"));
            opd.setDeleted(false);
            if (opd.getVanID() == null && vanID != null) { opd.setVanID(vanID); opd.setParkingPlaceID(parkingPlaceID); }
            opd.setProcessed("N");

            generalOpdRepo.save(opd);
            if (isNew) {
                generalOpdRepo.updateVanSerialNo(opd.getId());
                dualWriteOpdToStandardTables(opd, beneficiaryRegID, visit, createdBy, vanID, parkingPlaceID);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("beneficiaryRegID", beneficiaryRegID);
            results.add(result);
        }
        return results;
    }

    @Override
    public Map<String, Object> getGeneralOpd(Long beneficiaryRegID) throws Exception {
        List<StopTBGeneralOpd> opdList = generalOpdRepo.findLatestByBenRegID(beneficiaryRegID, PageRequest.of(0, 1));
        if (opdList.isEmpty()) throw new Exception("No general OPD record found for beneficiaryRegID: " + beneficiaryRegID);
        return opdToMap(opdList.get(0));
    }

    @Override
    public Map<String, Object> getAllGeneralOpd(Integer providerServiceMapID, Integer villageID) throws Exception {
        List<StopTBGeneralOpd> list = (villageID != null)
                ? generalOpdRepo.findAllByProviderServiceMapIDAndVillageID(providerServiceMapID, villageID)
                : generalOpdRepo.findAllByProviderServiceMapID(providerServiceMapID);
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
        m.put("updateDate", o.getLastModDate());
        m.put("updatedBy", o.getModifiedBy());
        return m;
    }

    // ── Nurse: Diagnostics ────────────────────────────────────────────────────

    @Override
    @Transactional
    public List<Map<String, Object>> saveDiagnostics(List<Map<String, Object>> dataList) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();
        Integer vanID = campConfigService.getVanID();
        Integer parkingPlaceID = campConfigService.getParkingPlaceID();
        for (Map<String, Object> data : dataList) {
            Long benRegID = getLong(data, "benRegID");
            if (benRegID == null) throw new Exception("benRegID is required");

            StopTBDiagnostics diag = diagnosticsRepo.findByBenRegID(benRegID);
            if (diag == null) diag = new StopTBDiagnostics();
            boolean isNew = diag.getId() == null;

            diag.setBenRegID(benRegID);
            diag.setProviderServiceMapID(getInt(data, "providerServiceMapID"));

            // PRD: date is user-provided, not editable once submitted
            if (diag.getVisitDate() == null) {
                Timestamp provided = getTimestamp(data, "visitDate");
                diag.setVisitDate(provided != null ? provided : new Timestamp(System.currentTimeMillis()));
            }

            diag.setNikshayId(getString(data, "nikshayId"));

            diag.setIsReferredForDigitalChestXray(getBool(data, "isReferredForDigitalChestXray"));
            diag.setReasonForDenialChestXray(getString(data, "reasonForDenialChestXray"));
            diag.setReasonForDenialChestXrayOther(getString(data, "reasonForDenialChestXrayOther"));
            diag.setIsDigitalChestXrayConducted(getBool(data, "isDigitalChestXrayConducted"));
            diag.setReasonNotConductedChestXray(getString(data, "reasonNotConductedChestXray"));
            diag.setReasonNotConductedChestXrayOther(getString(data, "reasonNotConductedChestXrayOther"));
            diag.setDigitalChestXrayResult(getString(data, "digitalChestXrayResult"));

            diag.setIsReferredForSputumCollection(getBool(data, "isReferredForSputumCollection"));
            diag.setReasonForDenialSputum(getString(data, "reasonForDenialSputum"));
            diag.setReasonForDenialSputumOther(getString(data, "reasonForDenialSputumOther"));
            diag.setSputumSubmittedAt(getString(data, "sputumSubmittedAt"));

            diag.setIsTruenatConducted(getBool(data, "isTruenatConducted"));
            diag.setReasonNotConductedNaat(getString(data, "reasonNotConductedNaat"));
            diag.setReasonNotConductedNaatOther(getString(data, "reasonNotConductedNaatOther"));
            diag.setTruenatResult(getString(data, "truenatResult"));

            diag.setRecommendedForLiquidCulture(getBool(data, "recommendedForLiquidCulture"));
            // PRD: liquid culture result can be updated after submission (results come after 40-45 days)
            if (data.containsKey("liquidCultureResult")) {
                diag.setLiquidCultureResult(getString(data, "liquidCultureResult"));
            }
            diag.setCreatedBy(getString(data, "createdBy"));
            diag.setModifiedBy(getString(data, "createdBy"));
            diag.setDeleted(false);
            if (diag.getVanID() == null && vanID != null) { diag.setVanID(vanID); diag.setParkingPlaceID(parkingPlaceID); }
            diag.setProcessed("N");

            diagnosticsRepo.save(diag);
            if (isNew) diagnosticsRepo.updateVanSerialNo(diag.getId());

            Map<String, Object> result = new HashMap<>();
            result.put("benRegID", benRegID);
            results.add(result);
        }
        return results;
    }

    @Override
    public Map<String, Object> getDiagnostics(Long benRegID) throws Exception {
        StopTBDiagnostics diag = diagnosticsRepo.findByBenRegID(benRegID);
        if (diag == null) throw new Exception("No diagnostics record found for benRegID: " + benRegID);
        return diagnosticsToMap(diag);
    }

    @Override
    public Map<String, Object> getAllDiagnostics(Integer providerServiceMapID, Integer villageID) throws Exception {
        List<StopTBDiagnostics> list = (villageID != null)
                ? diagnosticsRepo.findAllByProviderServiceMapIDAndVillageID(providerServiceMapID, villageID)
                : diagnosticsRepo.findAllByProviderServiceMapID(providerServiceMapID);
        List<Map<String, Object>> items = new ArrayList<>();
        for (StopTBDiagnostics d : list) items.add(diagnosticsToMap(d));
        Map<String, Object> result = new HashMap<>();
        result.put("data", items);
        result.put("count", items.size());
        return result;
    }

    private Map<String, Object> diagnosticsToMap(StopTBDiagnostics d) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", d.getId());
        m.put("benRegID", d.getBenRegID());
        m.put("providerServiceMapID", d.getProviderServiceMapID());
        m.put("visitDate", d.getVisitDate());
        m.put("nikshayId", d.getNikshayId());
        m.put("isReferredForDigitalChestXray", d.getIsReferredForDigitalChestXray());
        m.put("reasonForDenialChestXray", d.getReasonForDenialChestXray());
        m.put("reasonForDenialChestXrayOther", d.getReasonForDenialChestXrayOther());
        m.put("isDigitalChestXrayConducted", d.getIsDigitalChestXrayConducted());
        m.put("reasonNotConductedChestXray", d.getReasonNotConductedChestXray());
        m.put("reasonNotConductedChestXrayOther", d.getReasonNotConductedChestXrayOther());
        m.put("digitalChestXrayResult", d.getDigitalChestXrayResult());
        m.put("isReferredForSputumCollection", d.getIsReferredForSputumCollection());
        m.put("reasonForDenialSputum", d.getReasonForDenialSputum());
        m.put("reasonForDenialSputumOther", d.getReasonForDenialSputumOther());
        m.put("sputumSubmittedAt", d.getSputumSubmittedAt());
        m.put("isTruenatConducted", d.getIsTruenatConducted());
        m.put("reasonNotConductedNaat", d.getReasonNotConductedNaat());
        m.put("reasonNotConductedNaatOther", d.getReasonNotConductedNaatOther());
        m.put("truenatResult", d.getTruenatResult());
        m.put("recommendedForLiquidCulture", d.getRecommendedForLiquidCulture());
        m.put("liquidCultureResult", d.getLiquidCultureResult());
        m.put("createdBy", d.getCreatedBy());
        m.put("createdDate", d.getCreatedDate());
        m.put("updateDate", d.getLastModDate());
        m.put("updatedBy", d.getModifiedBy());
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

    // ── Standard table dual-writes ────────────────────────────────────────────

    private Map<String, Object> getRegistrationExtras(Long beneficiaryRegID) {
        try {
            RMNCHMBeneficiarymapping mapping = beneficiaryRepo.getById(BigInteger.valueOf(beneficiaryRegID));
            RMNCHMBeneficiarydetail detail = (mapping != null && mapping.getBenDetailsId() != null)
                    ? beneficiaryRepo.getDetailsById(mapping.getBenDetailsId())
                    : null;
            if (detail != null && detail.getOtherFields() != null && !detail.getOtherFields().isEmpty()) {
                return new ObjectMapper().readValue(detail.getOtherFields(), Map.class);
            }
        } catch (Exception e) {
            logger.warn("Cannot read otherFields for benRegID: " + beneficiaryRegID);
        }
        return Collections.emptyMap();
    }

    private void dualWriteExamToStandardTables(StopTBGeneralExamination exam, Long beneficiaryRegID,
            BenVisitDetail visit, String createdBy, Integer vanID, Integer parkingPlaceID) {
        try {
            Map<String, Object> extras = getRegistrationExtras(beneficiaryRegID);
            writeAnthropometry(extras, beneficiaryRegID, visit, createdBy, vanID, parkingPlaceID);
            writeVitals(exam, extras, beneficiaryRegID, visit, createdBy, vanID, parkingPlaceID);
            writePhyGeneralExam(exam, beneficiaryRegID, visit, createdBy, vanID, parkingPlaceID);
            if (Boolean.TRUE.equals(exam.getReferralToHWCNeeded())) {
                writeReferral(beneficiaryRegID, visit, createdBy, vanID, parkingPlaceID);
            }
        } catch (Exception e) {
            logger.error("Dual-write exam to standard tables failed for benRegID: " + beneficiaryRegID, e);
        }
    }

    private void writeAnthropometry(Map<String, Object> extras, Long beneficiaryRegID,
            BenVisitDetail visit, String createdBy, Integer vanID, Integer parkingPlaceID) {
        try {
            BenAnthropometryDetail a = new BenAnthropometryDetail();
            a.setBeneficiaryRegID(beneficiaryRegID);
            a.setBenVisitID(visit.getBenVisitId());
            a.setVisitCode(visit.getVisitCode());
            a.setProviderServiceMapID(visit.getProviderServiceMapID());
            a.setCreatedBy(createdBy);
            a.setVanID(vanID);
            a.setParkingPlaceID(parkingPlaceID);
            Object h = extras.get("height");
            Object w = extras.get("weight");
            Object b = extras.get("bmi");
            if (h instanceof Number) a.setHeightCm(((Number) h).doubleValue());
            if (w instanceof Number) a.setWeightKg(((Number) w).doubleValue());
            if (b instanceof Number) a.setBmi(((Number) b).doubleValue());
            benAnthropometryRepo.save(a);
        } catch (Exception e) {
            logger.error("writeAnthropometry failed for benRegID: " + beneficiaryRegID, e);
        }
    }

    private void writeVitals(StopTBGeneralExamination exam, Map<String, Object> extras,
            Long beneficiaryRegID, BenVisitDetail visit, String createdBy, Integer vanID, Integer parkingPlaceID) {
        try {
            BenPhysicalVitalDetail v = new BenPhysicalVitalDetail();
            v.setBeneficiaryRegID(beneficiaryRegID);
            v.setBenVisitID(visit.getBenVisitId());
            v.setVisitCode(visit.getVisitCode());
            v.setProviderServiceMapID(visit.getProviderServiceMapID());
            v.setCreatedBy(createdBy);
            v.setVanID(vanID);
            v.setParkingPlaceID(parkingPlaceID);
            Object t = extras.get("temperatureValue");
            if (t instanceof Number) v.setTemperature(((Number) t).doubleValue());
            if (exam.getPulseRate() != null) v.setPulseRate(exam.getPulseRate().shortValue());
            if (exam.getSystolicBP() != null) v.setSystolicBP(exam.getSystolicBP().shortValue());
            if (exam.getDiastolicBP() != null) v.setDiastolicBP(exam.getDiastolicBP().shortValue());
            if (exam.getRandomBloodSugar() != null) v.setBloodGlucoseRandom(exam.getRandomBloodSugar().shortValue());
            benPhysicalVitalRepo.save(v);
        } catch (Exception e) {
            logger.error("writeVitals failed for benRegID: " + beneficiaryRegID, e);
        }
    }

    private void writePhyGeneralExam(StopTBGeneralExamination exam, Long beneficiaryRegID,
            BenVisitDetail visit, String createdBy, Integer vanID, Integer parkingPlaceID) {
        try {
            PhyGeneralExamination g = new PhyGeneralExamination();
            g.setBeneficiaryRegID(beneficiaryRegID);
            g.setBenVisitID(visit.getBenVisitId());
            g.setVisitCode(visit.getVisitCode());
            g.setProviderServiceMapID(visit.getProviderServiceMapID());
            g.setCreatedBy(createdBy);
            g.setVanID(vanID);
            g.setParkingPlaceID(parkingPlaceID);
            g.setPallor(exam.getPallor());
            g.setJaundice(exam.getIcterus());
            g.setCyanosis(exam.getCyanosis());
            g.setClubbing(exam.getClubbing());
            g.setLymphadenopathy(exam.getLymphadenopathy());
            g.setEdema(exam.getOedema());
            phyGeneralExaminationRepo.save(g);
        } catch (Exception e) {
            logger.error("writePhyGeneralExam failed for benRegID: " + beneficiaryRegID, e);
        }
    }

    private void writeReferral(Long beneficiaryRegID, BenVisitDetail visit,
            String createdBy, Integer vanID, Integer parkingPlaceID) {
        try {
            BenReferDetails r = new BenReferDetails();
            r.setBeneficiaryRegID(beneficiaryRegID);
            r.setBenVisitID(visit.getBenVisitId());
            r.setVisitCode(visit.getVisitCode());
            r.setProviderServiceMapID(visit.getProviderServiceMapID());
            r.setCreatedBy(createdBy);
            r.setVanID(vanID);
            r.setParkingPlaceID(parkingPlaceID);
            r.setReferredToInstituteName("HWC");
            r.setReferralReason("Stop TB Referral");
            benReferDetailsRepo.save(r);
        } catch (Exception e) {
            logger.error("writeReferral failed for benRegID: " + beneficiaryRegID, e);
        }
    }

    private void dualWriteOpdToStandardTables(StopTBGeneralOpd opd, Long beneficiaryRegID,
            BenVisitDetail visit, String createdBy, Integer vanID, Integer parkingPlaceID) {
        try {
            writeChiefComplaint(opd.getChiefComplaint(), beneficiaryRegID, visit, createdBy, vanID, parkingPlaceID);
            writePrescription(opd, beneficiaryRegID, visit, createdBy, vanID, parkingPlaceID);
        } catch (Exception e) {
            logger.error("Dual-write OPD to standard tables failed for benRegID: " + beneficiaryRegID, e);
        }
    }

    private void writeChiefComplaint(String complaint, Long beneficiaryRegID, BenVisitDetail visit,
            String createdBy, Integer vanID, Integer parkingPlaceID) {
        if (complaint == null || complaint.isBlank()) return;
        try {
            BenChiefComplaint c = new BenChiefComplaint();
            c.setBeneficiaryRegID(beneficiaryRegID);
            c.setBenVisitID(visit.getBenVisitId());
            c.setVisitCode(visit.getVisitCode());
            c.setProviderServiceMapID(visit.getProviderServiceMapID());
            c.setCreatedBy(createdBy);
            c.setVanID(vanID);
            c.setParkingPlaceID(parkingPlaceID);
            c.setChiefComplaint(complaint);
            benChiefComplaintRepo.save(c);
        } catch (Exception e) {
            logger.error("writeChiefComplaint failed for benRegID: " + beneficiaryRegID, e);
        }
    }

    private void writePrescription(StopTBGeneralOpd opd, Long beneficiaryRegID, BenVisitDetail visit,
            String createdBy, Integer vanID, Integer parkingPlaceID) {
        if (opd.getMedication() == null || opd.getMedication().isBlank()) return;
        try {
            PrescriptionDetail p = new PrescriptionDetail();
            p.setBeneficiaryRegID(beneficiaryRegID);
            p.setBenVisitID(visit.getBenVisitId());
            p.setVisitCode(visit.getVisitCode());
            p.setProviderServiceMapID(visit.getProviderServiceMapID());
            p.setCreatedBy(createdBy);
            p.setVanID(vanID);
            p.setParkingPlaceID(parkingPlaceID);
            p.setInstruction(opd.getNotes());
            p = prescriptionDetailRepo.save(p);

            PrescribedDrugDetail d = new PrescribedDrugDetail();
            d.setBeneficiaryRegID(beneficiaryRegID);
            d.setBenVisitID(visit.getBenVisitId());
            d.setVisitCode(visit.getVisitCode());
            d.setProviderServiceMapID(visit.getProviderServiceMapID());
            d.setCreatedBy(createdBy);
            d.setVanID(vanID);
            d.setParkingPlaceID(parkingPlaceID);
            d.setPrescriptionID(p.getPrescriptionID());
            d.setDrugName(opd.getMedication());
            d.setDose(opd.getDosage());
            d.setFrequency(opd.getFrequency());
            d.setDuration(opd.getDuration());
            prescribedDrugDetailRepo.save(d);
        } catch (Exception e) {
            logger.error("writePrescription failed for benRegID: " + beneficiaryRegID, e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Timestamp parseDob(JsonObject obj) {
        if (!obj.has("dob") || obj.get("dob").isJsonNull()) return null;
        try { return new Timestamp(obj.get("dob").getAsLong()); } catch (Exception ignored) {}
        try { return Timestamp.valueOf(obj.get("dob").getAsString().replace("T", " ").replace("Z", "").trim()); } catch (Exception ignored) {}
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

    private Timestamp getTimestamp(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return null;
        if (v instanceof Number) return new Timestamp(((Number) v).longValue());
        try { return new Timestamp(Long.parseLong(v.toString())); } catch (Exception e) { return null; }
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
