package com.iemr.flw.service.impl;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.iemr.flw.domain.iemr.EyeCheckupVisit;
import com.iemr.flw.dto.iemr.EyeCheckupListDTO;
import com.iemr.flw.dto.iemr.EyeCheckupRequestDTO;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.flw.domain.identity.BenHealthIDDetails;
import com.iemr.flw.domain.identity.RMNCHBeneficiaryDetailsRmnch;
import com.iemr.flw.domain.identity.RMNCHBornBirthDetails;
import com.iemr.flw.domain.identity.RMNCHHouseHoldDetails;
import com.iemr.flw.domain.identity.RMNCHMBeneficiaryAccount;
import com.iemr.flw.domain.identity.RMNCHMBeneficiaryImage;
import com.iemr.flw.domain.identity.RMNCHMBeneficiaryaddress;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarycontact;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarymapping;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.mapper.InputMapper;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.identity.HouseHoldRepo;
import com.iemr.flw.service.BeneficiaryService;
import com.iemr.flw.utils.http.HttpUtils;

@Service
@Qualifier("rmnchServiceImpl")
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private static final Logger logger = LoggerFactory.getLogger(BeneficiaryServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Gson GSON = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();

    // Map keys as constants to avoid typos and magic strings
    private static final String KEY_HOUSEHOLD_DETAILS  = "householdDetails";
    private static final String KEY_BORN_BIRTH_DETAILS = "bornBirthDetails";
    private static final String KEY_BENEFICIARY_DETAILS = "beneficiaryDetails";
    private static final String KEY_ABHA_HEALTH_DETAILS = "abhaHealthDetails";

    @Value("${door-to-door-page-size}")
    private String doorToDoorPageSize;

    @Value("${fhir-url}")
    private String fhirUrl;

    @Value("${getHealthID}")
    private String getHealthID;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private HouseHoldRepo houseHoldRepo;

    @Autowired
    private GeneralOpdRepo generalOpdRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;

    @Autowired
    private EyeCheckUpVisitRepo eyeCheckUpVisitRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceRoleRepo userRepo;

    // Single shared HttpUtils instance — avoid creating one per request
    private final HttpUtils httpUtils = new HttpUtils();

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    @Override
    public String getBenData(GetBenRequestHandler request, String authorisation) throws Exception {
        if (request == null || request.getAshaId() == null) {
            throw new Exception("Invalid/missing village details");
        }
        if (request.getPageNo() == null) {
            throw new Exception("Invalid page no");
        }

        String userName = beneficiaryRepo.getUserName(request.getAshaId());
        if (userName == null || userName.isEmpty()) {
            throw new Exception("Asha details not found, please contact administrator");
        }
        request.setUserName(userName);

        int pageSize = Integer.parseInt(doorToDoorPageSize);
        PageRequest pr = PageRequest.of(request.getPageNo(), pageSize);

        Page<RMNCHMBeneficiaryaddress> page;
        if (request.getFromDate() != null && request.getToDate() != null) {
            page = beneficiaryRepo.getBenDataWithinDates(
                    request.getUserName(), request.getFromDate(), request.getToDate(), pr);
        } else {
            page = beneficiaryRepo.getBenDataByUser(request.getUserName(), pr);
        }

        List<RMNCHMBeneficiaryaddress> resultSet = page.getContent();
        if (resultSet.isEmpty()) {
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("data", new ArrayList<>());
            emptyResponse.put("pageSize", pageSize);
            emptyResponse.put("totalPage", 0);
            return GSON.toJson(emptyResponse);
        }

        return buildBeneficiaryResponse(resultSet, page.getTotalPages(), authorisation);
    }

    @Override
    public String saveEyeCheckupVsit(List<EyeCheckupRequestDTO> eyeCheckupRequestDTOS) {
        if (eyeCheckupRequestDTOS == null || eyeCheckupRequestDTOS.isEmpty()) {
            throw new IllegalArgumentException("Eye checkup list must not be empty");
        }

        // Resolve userId once — same ASHA for the whole batch
        Integer userId = userRepo.getUserIdByName(jwtUtil.getUserNameFromStorage());

        List<EyeCheckupVisit> visitsToSave = eyeCheckupRequestDTOS.stream().map(dto -> {
            EyeCheckupListDTO f = dto.getFields();
            EyeCheckupVisit visit = new EyeCheckupVisit();
            visit.setBeneficiaryId(dto.getBeneficiaryId());
            visit.setHouseholdId(dto.getHouseHoldId());
            visit.setUserId(userId);
            visit.setCreatedBy(dto.getUserName());
            visit.setVisitDate(LocalDate.parse(f.getVisit_date(), FORMATTER));
            visit.setSymptomsObserved(f.getSymptoms_observed());
            visit.setEyeAffected(f.getEye_affected());
            visit.setReferredTo(f.getReferred_to());
            // No-op StringBuilder removed; just use the value directly
            visit.setDischargeSummaryUpload(f.getDischarge_summary_upload());
            visit.setFollowUpStatus(f.getFollow_up_status());
            visit.setDateOfSurgery(f.getDate_of_surgery());
            return visit;
        }).collect(Collectors.toList());

        // Batch save instead of one-by-one
        eyeCheckUpVisitRepo.saveAll(visitsToSave);
        return "Eye checkup data saved successfully.";
    }

    @Override
    public List<EyeCheckupRequestDTO> getEyeCheckUpVisit(GetBenRequestHandler request) {
        List<EyeCheckupVisit> visits = eyeCheckUpVisitRepo.findByUserId(request.getAshaId());
        return visits.stream().map(this::mapVisitToDto).collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String buildBeneficiaryResponse(List<RMNCHMBeneficiaryaddress> addressList,
                                            int totalPage, String authorisation) {
        List<Map<String, Object>> resultList = new ArrayList<>(addressList.size());

        for (RMNCHMBeneficiaryaddress address : addressList) {
            try {
                RMNCHMBeneficiarymapping mapping = beneficiaryRepo.getByAddressID(address.getId());
                if (mapping == null) continue;

                Map<String, Object> resultMap = buildResultMap(mapping, authorisation);
                resultList.add(resultMap);
            } catch (Exception e) {
                logger.error("Error processing addressID: {} vanID: {} — {}",
                        address.getId(), address.getVanID(), e.getMessage(), e);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", resultList);
        response.put("pageSize", Integer.parseInt(doorToDoorPageSize));
        response.put("totalPage", totalPage);
        return GSON.toJson(response);
    }

    /**
     * Builds the complete result map for one beneficiary mapping.
     * All sub-fetches are grouped here so the flow is easy to follow.
     */
    private Map<String, Object> buildResultMap(RMNCHMBeneficiarymapping m, String authorisation) {
        // ---- Fetch sub-objects (null-safe defaults) ----
        RMNCHMBeneficiarydetail  benDetail  = fetchOrDefault(m.getBenDetailsId(),
                beneficiaryRepo::getDetailsById, new RMNCHMBeneficiarydetail());
        RMNCHMBeneficiaryAccount benAccount = fetchOrDefault(m.getBenAccountID(),
                beneficiaryRepo::getAccountById, new RMNCHMBeneficiaryAccount());
        RMNCHMBeneficiaryImage   benImage   = m.getBenImageId() != null
                ? beneficiaryRepo.getImageById(m.getBenImageId().longValue()) : new RMNCHMBeneficiaryImage();
        RMNCHMBeneficiaryaddress benAddress = fetchOrDefault(m.getBenAddressId(),
                beneficiaryRepo::getAddressById, new RMNCHMBeneficiaryaddress());
        RMNCHMBeneficiarycontact benContact = fetchOrDefault(m.getBenContactsId(),
                beneficiaryRepo::getContactById, new RMNCHMBeneficiarycontact());

        BigInteger benID = m.getBenRegId() != null
                ? beneficiaryRepo.getBenIdFromRegID(m.getBenRegId().longValue()) : null;

        RMNCHBeneficiaryDetailsRmnch benDetailsRMNCH = new RMNCHBeneficiaryDetailsRmnch();
        RMNCHHouseHoldDetails        householdDetails = new RMNCHHouseHoldDetails();
        RMNCHBornBirthDetails        bornBirthDetails = new RMNCHBornBirthDetails();

        if (m.getBenRegId() != null) {
            RMNCHBeneficiaryDetailsRmnch fetched =
                    beneficiaryRepo.getDetailsByRegID(m.getBenRegId().longValue());
            if (fetched != null) {
                benDetailsRMNCH = fetched;
                if (benDetailsRMNCH.getHouseoldId() != null) {
                    householdDetails = houseHoldRepo.getByHouseHoldID(benDetailsRMNCH.getHouseoldId());
                }
            }
            RMNCHBornBirthDetails birth = beneficiaryRepo.getBornBirthByRegID(m.getBenRegId().longValue());
            if (birth != null) bornBirthDetails = birth;
        }

        // ---- Merge fields into benDetailsRMNCH ----
        mergeDetailFields(benDetailsRMNCH, benDetail, benAccount, benAddress, benContact, benImage, benID);

        // ---- Age calculation ----
        applyAgeCalculation(benDetailsRMNCH, benDetail);

        // ---- Health details ----
        Map<String, Object> healthDetails = getBenHealthDetails(m.getBenRegId());

        // ---- Assemble result map ----
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(KEY_HOUSEHOLD_DETAILS,  householdDetails != null ? householdDetails : new HashMap<>());
        resultMap.put(KEY_BORN_BIRTH_DETAILS, bornBirthDetails);
        resultMap.put(KEY_BENEFICIARY_DETAILS, benDetailsRMNCH);
        resultMap.put(KEY_ABHA_HEALTH_DETAILS, healthDetails);
        resultMap.put("houseoldId",       benDetailsRMNCH.getHouseoldId());
        resultMap.put("benficieryid",     benDetailsRMNCH.getBenficieryid());
        resultMap.put("BenRegId",          m.getBenRegId());
        resultMap.put("isDeath",          benDetailsRMNCH.getIsDeath());
        resultMap.put("isDeathValue",     benDetailsRMNCH.getIsDeathValue());
        resultMap.put("dateOfDeath",      benDetailsRMNCH.getDateOfDeath());
        resultMap.put("timeOfDeath",      benDetailsRMNCH.getTimeOfDeath());
        resultMap.put("reasonOfDeath",    benDetailsRMNCH.getReasonOfDeath());
        resultMap.put("reasonOfDeathId",  benDetailsRMNCH.getReasonOfDeathId());
        resultMap.put("placeOfDeath",     benDetailsRMNCH.getPlaceOfDeath());
        resultMap.put("placeOfDeathId",   benDetailsRMNCH.getPlaceOfDeathId());
        resultMap.put("isSpouseAdded",    benDetailsRMNCH.getIsSpouseAdded());
        resultMap.put("isChildrenAdded",  benDetailsRMNCH.getIsChildrenAdded());
        resultMap.put("noOfchildren",     benDetailsRMNCH.getNoOfchildren());
        resultMap.put("isMarried",        benDetailsRMNCH.getIsMarried());
        resultMap.put("doYouHavechildren", benDetailsRMNCH.getDoYouHavechildren());
        resultMap.put("noofAlivechildren", benDetailsRMNCH.getNoofAlivechildren()); // typo fixed (no trailing spaces)

        // ASHA id from address creator
        if (benAddress.getCreatedBy() != null) {
            Integer userID = beneficiaryRepo.getUserIDByUserName(benAddress.getCreatedBy());
            if (userID != null && userID > 0) resultMap.put("ashaId", userID);
        }

        // External ABHA fetch
        if (m.getBenRegId() != null) {
            fetchHealthIdByBenRegID(m.getBenRegId().longValue(), authorisation, resultMap);
        }

        return resultMap;
    }

    /** Copies all mergeable fields from sub-objects into the RMNCH details object. */
    private void mergeDetailFields(RMNCHBeneficiaryDetailsRmnch dest,
                                   RMNCHMBeneficiarydetail  detail,
                                   RMNCHMBeneficiaryAccount account,
                                   RMNCHMBeneficiaryaddress address,
                                   RMNCHMBeneficiarycontact contact,
                                   RMNCHMBeneficiaryImage   image,
                                   BigInteger               benID) {
        // Personal
        setIfNotNull(detail.getFirstName(),      dest::setFirstName);
        setIfNotNull(detail.getLastName(),       dest::setLastName);
        setIfNotNull(detail.getMotherName(),     dest::setMotherName);
        setIfNotNull(detail.getFatherName(),     dest::setFatherName);
        setIfNotNull(detail.getSpousename(),     dest::setSpousename);
        setIfNotNull(detail.getDob(),            dest::setDob);
        setIfNotNull(detail.getGender(),         dest::setGender);
        setIfNotNull(detail.getGenderId(),       dest::setGenderId);
        setIfNotNull(detail.getMaritalstatus(),  dest::setMaritalstatus);
        setIfNotNull(detail.getMaritalstatusId(),dest::setMaritalstatusId);
        setIfNotNull(detail.getMarriageDate(),   dest::setMarriageDate);
        setIfNotNull(detail.getLiteracyStatus(), dest::setLiteracyStatus);
        setIfNotNull(detail.getCommunity(),      dest::setCommunity);
        setIfNotNull(detail.getCommunityId(),    dest::setCommunityId);
        setIfNotNull(detail.getReligion(),       dest::setReligion);
        setIfNotNull(detail.getReligionID(),     dest::setReligionID);
        if (dest.getCreatedBy() == null) setIfNotNull(detail.getCreatedBy(), dest::setCreatedBy);

        // Bank
        setIfNotNull(account.getNameOfBank(),  dest::setNameOfBank);
        setIfNotNull(account.getBranchName(),  dest::setBranchName);
        setIfNotNull(account.getIfscCode(),    dest::setIfscCode);
        setIfNotNull(account.getBankAccount(), dest::setBankAccount);

        // Address
        setIfNotNull(address.getCountyid(),           dest::setCountryId);
        setIfNotNull(address.getPermCountry(),         dest::setCountryName);
        setIfNotNull(address.getStatePerm(),           dest::setStateId);
        setIfNotNull(address.getPermState(),           dest::setStateName);
        setIfNotNull(address.getDistrictidPerm(),      dest::setDistrictid);
        setIfNotNull(address.getDistrictnamePerm(),    dest::setDistrictname);
        setIfNotNull(address.getPermSubDistrictId(),   dest::setBlockId);
        setIfNotNull(address.getPermSubDistrict(),     dest::setBlockName);
        setIfNotNull(address.getVillageidPerm(),       dest::setVillageId);
        setIfNotNull(address.getVillagenamePerm(),     dest::setVillageName);
        setIfNotNull(address.getPermServicePointId(),  dest::setServicePointID);
        setIfNotNull(address.getPermServicePoint(),    dest::setServicePointName);
        setIfNotNull(address.getPermZoneID(),          dest::setZoneID);
        setIfNotNull(address.getPermZone(),            dest::setZoneName);
        setIfNotNull(address.getPermAddrLine1(),       dest::setAddressLine1);
        setIfNotNull(address.getPermAddrLine2(),       dest::setAddressLine2);
        setIfNotNull(address.getPermAddrLine3(),       dest::setAddressLine3);

        // Contact
        setIfNotNull(contact.getPreferredPhoneNum(), dest::setContact_number);

        // Image
        if (image != null) setIfNotNull(image.getUser_image(), dest::setUser_image);

        // Ben ID
        if (benID != null) dest.setBenficieryid(benID.longValue());

        // Related beneficiary IDs
        if (dest.getRelatedBeneficiaryIdsDB() != null) {
            String[] parts = dest.getRelatedBeneficiaryIdsDB().split(",");
            Long[] ids = new Long[parts.length];
            for (int i = 0; i < parts.length; i++) ids[i] = Long.valueOf(parts[i].trim());
            dest.setRelatedBeneficiaryIds(ids);
        }
    }

    /** Calculates age from DOB and sets ageFull, age, and age_unit on the destination object. */
    private void applyAgeCalculation(RMNCHBeneficiaryDetailsRmnch dest, RMNCHMBeneficiarydetail detail) {
        if (detail.getDob() == null) return;

        Calendar cal = Calendar.getInstance();
        cal.setTime(detail.getDob());
        LocalDate birthDate = LocalDate.of(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));

        Period p = Period.between(birthDate, LocalDate.now());
        int years  = p.getYears();
        int months = p.getMonths();
        int days   = p.getDays();

        String ageFull;
        int ageVal;
        String ageUnit;

        if (years > 0) {
            ageFull = years + " years - " + months + " months";
            ageVal  = years;
            ageUnit = years > 1 ? "Years" : "Year";
        } else if (months > 0) {
            ageFull = months + " months - " + days + " days";
            ageVal  = months;
            ageUnit = months > 1 ? "Months" : "Month";
        } else {
            ageFull = days + " days";
            ageVal  = days;
            ageUnit = days > 1 ? "Days" : "Day";
        }

        dest.setAgeFull(ageFull);
        dest.setAge(ageVal);
        dest.setAge_unit(ageUnit);
    }

    private Map<String, Object> getBenHealthDetails(BigInteger benRegId) {
        Map<String, Object> healthDetails = new HashMap<>();
        if (benRegId == null) return healthDetails;

        Object[] benHealthIdNumber = beneficiaryRepo.getBenHealthIdNumber(benRegId);
        if (benHealthIdNumber == null || benHealthIdNumber.length == 0) return healthDetails;

        Object[] healthData      = (Object[]) benHealthIdNumber[0];
        String   healthIdNumber  = healthData[0] != null ? healthData[0].toString() : null;
        String   healthId        = healthData[1] != null ? healthData[1].toString() : null;

        if (healthIdNumber == null) return healthDetails;

        List<Object[]> health = beneficiaryRepo.getBenHealthDetails(healthIdNumber);
        if (health != null && !health.isEmpty()) {
            Object[] row = health.get(0); // last writer wins anyway; just use first
            healthDetails.put("HealthID",       row[0]);
            healthDetails.put("HealthIdNumber", row[1]);
            healthDetails.put("isNewAbha",      row[2]);
        } else {
            healthDetails.put("HealthIdNumber", healthIdNumber);
            healthDetails.put("HealthID",       healthId);
            healthDetails.put("isNewAbha",      false);
        }
        return healthDetails;
    }

    public void fetchHealthIdByBenRegID(Long benRegID, String authorization, Map<String, Object> resultMap) {
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("beneficiaryRegID", benRegID);
        requestBody.put("beneficiaryID", null);

        try {
            HashMap<String, Object> headers = new HashMap<>();
            headers.put("Authorization", authorization);
            String responseStr = httpUtils.post(fhirUrl + "/" + getHealthID, GSON.toJson(requestBody), headers);

            JsonElement element = JsonParser.parseString(responseStr); // non-deprecated API
            if (!element.isJsonObject()) return;

            JsonObject root = element.getAsJsonObject();
            if (root.get("data") == null) return;

            JsonObject data = root.getAsJsonObject("data");
            if (data.get("BenHealthDetails") == null) return;

            BenHealthIDDetails[] details = InputMapper.gson().fromJson(
                    GSON.toJson(data.get("BenHealthDetails")), BenHealthIDDetails[].class);

            for (BenHealthIDDetails d : details) {
                if (d.getHealthId() != null)       resultMap.put("healthId",       d.getHealthId());
                if (d.getHealthIdNumber() != null) resultMap.put("healthIdNumber", d.getHealthIdNumber());
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch ABHA for benRegID {}: {}", benRegID, e.getMessage());
        }
    }

    private EyeCheckupRequestDTO mapVisitToDto(EyeCheckupVisit v) {
        EyeCheckupRequestDTO dto = new EyeCheckupRequestDTO();
        dto.setId(v.getId());
        dto.setBeneficiaryId(v.getBeneficiaryId());
        dto.setHouseHoldId(v.getHouseholdId());
        dto.setUserName(v.getCreatedBy());
        dto.setVisitDate(v.getVisitDate().format(FORMATTER));

        EyeCheckupListDTO fields = new EyeCheckupListDTO();
        fields.setVisit_date(v.getVisitDate().format(FORMATTER));
        fields.setSymptoms_observed(v.getSymptomsObserved());
        fields.setEye_affected(v.getEyeAffected());
        fields.setReferred_to(v.getReferredTo());
        fields.setFollow_up_status(v.getFollowUpStatus());
        fields.setDate_of_surgery(v.getDateOfSurgery());
        fields.setDischarge_summary_upload(v.getDischargeSummaryUpload());

        dto.setFields(fields);
        return dto;
    }

    // -------------------------------------------------------------------------
    // Generic utilities
    // -------------------------------------------------------------------------

    /** Calls setter only when value is non-null — replaces the repetitive if-blocks. */
    private <T> void setIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    /** Fetches an entity by ID; returns defaultValue when ID is null. */
    private <ID, T> T fetchOrDefault(ID id, java.util.function.Function<ID, T> fetcher, T defaultValue) {
        if (id == null) return defaultValue;
        T result = fetcher.apply(id);
        return result != null ? result : defaultValue;
    }
}