package com.iemr.flw.service.impl;

import java.math.BigInteger;
import java.sql.Date;
import java.text.SimpleDateFormat;
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
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.dto.iemr.EyeCheckupListDTO;
import com.iemr.flw.dto.iemr.EyeCheckupRequestDTO;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
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
import com.iemr.flw.utils.config.ConfigProperties;
import com.iemr.flw.utils.http.HttpUtils;

@Service
@Qualifier("rmnchServiceImpl")

public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final Logger logger = LoggerFactory.getLogger(BeneficiaryServiceImpl.class);
    @Value("${door-to-door-page-size}")
    private String door_to_door_page_size;


    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("MMM dd, yyyy h:mm:ss a").create();

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
    IncentivesRepo incentivesRepo;

    @Autowired
    IncentiveRecordRepo recordRepo;

    @Autowired
    private EyeCheckUpVisitRepo eyeCheckUpVisitRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceRoleRepo userRepo;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");



    @Override
    public String getBenData(GetBenRequestHandler request, String authorisation) throws Exception {

        // ✅ Early validation — no nested if/else pyramid
        if (request == null || request.getAshaId() == null)
            throw new Exception("Invalid/missing village details");
        if (request.getPageNo() == null)
            throw new Exception("Invalid page no");

        String userName = beneficiaryRepo.getUserName(request.getAshaId());
        if (userName == null || userName.isEmpty())
            throw new Exception("Asha details not found, please contact administrator");

        request.setUserName(userName);

        int pageSize = Integer.parseInt(door_to_door_page_size); // ✅ parse once
        PageRequest pr = PageRequest.of(request.getPageNo(), pageSize);

        // ✅ Ternary to pick query — removes duplicate Page<> variable declarations
        Page<RMNCHMBeneficiaryaddress> page =
                (request.getFromDate() != null && request.getToDate() != null)
                        ? beneficiaryRepo.getBenDataWithinDates(
                        request.getUserName(), request.getFromDate(), request.getToDate(), pr)
                        : beneficiaryRepo.getBenDataByUser(request.getUserName(), pr);

        List<RMNCHMBeneficiaryaddress> resultSet = page.getContent();

        // ✅ isEmpty() instead of size() > 0
        if (resultSet == null || resultSet.isEmpty()) {
            // Return empty response instead of null
            Map<String, Object> empty = new HashMap<>();
            empty.put("data", new ArrayList<>());
            empty.put("pageSize", pageSize);
            empty.put("totalPage", 0);
            return GSON.toJson(empty);
        }

        return getMappingsForAddressIDs(resultSet, page.getTotalPages(), authorisation);
    }

// ---------------------------------------------------------------
// PRIVATE METHOD
// ---------------------------------------------------------------

    private String getMappingsForAddressIDs(List<RMNCHMBeneficiaryaddress> addressList,
                                            int totalPage, String authorisation) {

        List<Map<String, Object>> resultList = new ArrayList<>(addressList.size()); // ✅ pre-sized

        for (RMNCHMBeneficiaryaddress a : addressList) {
            try {
                RMNCHMBeneficiarymapping m = beneficiaryRepo.getByAddressID(a.getId());
                if (m == null) continue; // ✅ skip instead of deeply nested if block

                Map<String, Object> resultMap = buildResultMap(m, authorisation);
                resultList.add(resultMap);

            } catch (Exception e) {
                // ✅ Fixed log format — message was concatenated before addressID making it unreadable
                logger.error("Error processing addressID: {}, vanID: {}, reason: {}",
                        a.getId(), a.getVanID(), e.getMessage());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", resultList);
        response.put("pageSize", Integer.parseInt(door_to_door_page_size));
        response.put("totalPage", totalPage);
        return GSON.toJson(response); // ✅ reuse static GSON instance
    }

// ---------------------------------------------------------------
// EXTRACTED: build one beneficiary's result map
// ---------------------------------------------------------------

    private Map<String, Object> buildResultMap(RMNCHMBeneficiarymapping m, String authorisation) {

        // ---- Fetch sub-objects (null-safe defaults) ----
        RMNCHMBeneficiarydetail  benDetail  = m.getBenDetailsId()  != null
                ? beneficiaryRepo.getDetailsById(m.getBenDetailsId())   : new RMNCHMBeneficiarydetail();
        RMNCHMBeneficiaryAccount benAccount = m.getBenAccountID()  != null
                ? beneficiaryRepo.getAccountById(m.getBenAccountID())   : new RMNCHMBeneficiaryAccount();
        RMNCHMBeneficiaryImage   benImage   = m.getBenImageId()    != null
                ? beneficiaryRepo.getImageById(m.getBenImageId().longValue()) : new RMNCHMBeneficiaryImage();
        RMNCHMBeneficiaryaddress benAddress = m.getBenAddressId()  != null
                ? beneficiaryRepo.getAddressById(m.getBenAddressId())   : new RMNCHMBeneficiaryaddress();
        RMNCHMBeneficiarycontact benContact = m.getBenContactsId() != null
                ? beneficiaryRepo.getContactById(m.getBenContactsId())  : new RMNCHMBeneficiarycontact();

        // ---- Protect against null returns from repo ----
        if (benDetail  == null) benDetail  = new RMNCHMBeneficiarydetail();
        if (benAccount == null) benAccount = new RMNCHMBeneficiaryAccount();
        if (benAddress == null) benAddress = new RMNCHMBeneficiaryaddress();
        if (benContact == null) benContact = new RMNCHMBeneficiarycontact();

        BigInteger benID = m.getBenRegId() != null
                ? beneficiaryRepo.getBenIdFromRegID(m.getBenRegId().longValue()) : null;

        // ---- RMNCH details + household + birth ----
        RMNCHBeneficiaryDetailsRmnch benRMNCH   = new RMNCHBeneficiaryDetailsRmnch();
        RMNCHHouseHoldDetails        household  = new RMNCHHouseHoldDetails();
        RMNCHBornBirthDetails        bornBirth  = new RMNCHBornBirthDetails();

        if (m.getBenRegId() != null) {
            RMNCHBeneficiaryDetailsRmnch fetched =
                    beneficiaryRepo.getDetailsByRegID(m.getBenRegId().longValue());
            if (fetched != null) {
                benRMNCH = fetched;
                if (benRMNCH.getHouseoldId() != null)
                    household = houseHoldRepo.getByHouseHoldID(benRMNCH.getHouseoldId());
            }
            RMNCHBornBirthDetails birth = beneficiaryRepo.getBornBirthByRegID(m.getBenRegId().longValue());
            if (birth != null) bornBirth = birth;
        }

        // ---- Merge all sub-object fields into benRMNCH ----
        mergeFields(benRMNCH, benDetail, benAccount, benAddress, benContact, benImage, benID);

        // ---- Age ----
        applyAge(benRMNCH, benDetail);

        // ---- Health details from local DB ----
        Map<String, Object> healthDetails = getBenHealthDetails(m.getBenRegId());

        // ---- Build result map ----
        Map<String, Object> map = new HashMap<>();
        map.put("householdDetails",   household != null ? household : new HashMap<>());
        map.put("bornbirthDeatils",   bornBirth != null ? bornBirth : new HashMap<>());  // key kept as-is for backward compat
        map.put("beneficiaryDetails", benRMNCH);
        map.put("abhaHealthDetails",  healthDetails);
        map.put("houseoldId",         benRMNCH.getHouseoldId());
        map.put("benficieryid",       benRMNCH.getBenficieryid());
        map.put("isDeath",            benRMNCH.getIsDeath());
        map.put("isDeathValue",       benRMNCH.getIsDeathValue());
        map.put("dateOfDeath",        benRMNCH.getDateOfDeath());
        map.put("timeOfDeath",        benRMNCH.getTimeOfDeath());
        map.put("reasonOfDeath",      benRMNCH.getReasonOfDeath());
        map.put("reasonOfDeathId",    benRMNCH.getReasonOfDeathId());
        map.put("placeOfDeath",       benRMNCH.getPlaceOfDeath());
        map.put("placeOfDeathId",     benRMNCH.getPlaceOfDeathId());
        map.put("isSpouseAdded",      benRMNCH.getIsSpouseAdded());
        map.put("isChildrenAdded",    benRMNCH.getIsChildrenAdded());
        map.put("noOfchildren",       benRMNCH.getNoOfchildren());
        map.put("isMarried",          benRMNCH.getIsMarried());
        map.put("doYouHavechildren",  benRMNCH.getDoYouHavechildren());
        map.put("noofAlivechildren",  benRMNCH.getNoofAlivechildren()); // ✅ trailing spaces removed
        map.put("isDeactivate",       benRMNCH.getIsDeactivate());
        map.put("BenRegId",           m.getBenRegId());

        // ✅ ASHA id lookup
        if (benAddress.getCreatedBy() != null) {
            Integer userID = beneficiaryRepo.getUserIDByUserName(benAddress.getCreatedBy());
            if (userID != null && userID > 0) map.put("ashaId", userID);
        }

        // ✅ External ABHA fetch
        if (m.getBenRegId() != null)
            fetchHealthIdByBenRegID(m.getBenRegId().longValue(), authorisation, map);

        return map;
    }

// ---------------------------------------------------------------
// EXTRACTED: merge all sub-object fields into RMNCH object
// replaces ~50 repetitive if (x != null) dest.setX(x) blocks
// ---------------------------------------------------------------

    private void mergeFields(RMNCHBeneficiaryDetailsRmnch d,
                             RMNCHMBeneficiarydetail  detail,
                             RMNCHMBeneficiaryAccount account,
                             RMNCHMBeneficiaryaddress address,
                             RMNCHMBeneficiarycontact contact,
                             RMNCHMBeneficiaryImage   image,
                             BigInteger               benID) {

        // Personal details
        ifNotNull(detail.getFirstName(),       d::setFirstName);
        ifNotNull(detail.getLastName(),        d::setLastName);
        ifNotNull(detail.getMotherName(),      d::setMotherName);
        ifNotNull(detail.getFatherName(),      d::setFatherName);
        ifNotNull(detail.getSpousename(),      d::setSpousename);
        ifNotNull(detail.getDob(),             d::setDob);
        ifNotNull(detail.getGender(),          d::setGender);
        ifNotNull(detail.getGenderId(),        d::setGenderId);
        ifNotNull(detail.getMaritalstatus(),   d::setMaritalstatus);
        ifNotNull(detail.getMaritalstatusId(), d::setMaritalstatusId);
        ifNotNull(detail.getMarriageDate(),    d::setMarriageDate);
        ifNotNull(detail.getLiteracyStatus(),  d::setLiteracyStatus);
        ifNotNull(detail.getCommunity(),       d::setCommunity);
        ifNotNull(detail.getCommunityId(),     d::setCommunityId);
        ifNotNull(detail.getReligion(),        d::setReligion);
        ifNotNull(detail.getReligionID(),      d::setReligionID);
        // createdBy — only set if not already populated from RMNCH fetch
        if (d.getCreatedBy() == null) ifNotNull(detail.getCreatedBy(), d::setCreatedBy);

        // Bank details
        ifNotNull(account.getNameOfBank(),  d::setNameOfBank);
        ifNotNull(account.getBranchName(),  d::setBranchName);
        ifNotNull(account.getIfscCode(),    d::setIfscCode);
        ifNotNull(account.getBankAccount(), d::setBankAccount);

        // Address / location
        ifNotNull(address.getCountyid(),          d::setCountryId);
        ifNotNull(address.getPermCountry(),        d::setCountryName);
        ifNotNull(address.getStatePerm(),          d::setStateId);
        ifNotNull(address.getPermState(),          d::setStateName);
        ifNotNull(address.getDistrictidPerm(),     d::setDistrictid);
        ifNotNull(address.getDistrictnamePerm(),   d::setDistrictname);
        ifNotNull(address.getPermSubDistrictId(),  d::setBlockId);
        ifNotNull(address.getPermSubDistrict(),    d::setBlockName);
        ifNotNull(address.getVillageidPerm(),      d::setVillageId);
        ifNotNull(address.getVillagenamePerm(),    d::setVillageName);
        ifNotNull(address.getPermServicePointId(), d::setServicePointID);
        ifNotNull(address.getPermServicePoint(),   d::setServicePointName);
        ifNotNull(address.getPermZoneID(),         d::setZoneID);
        ifNotNull(address.getPermZone(),           d::setZoneName);
        ifNotNull(address.getPermAddrLine1(),      d::setAddressLine1);
        ifNotNull(address.getPermAddrLine2(),      d::setAddressLine2);
        ifNotNull(address.getPermAddrLine3(),      d::setAddressLine3);

        // Contact
        ifNotNull(contact.getPreferredPhoneNum(), d::setContact_number);

        // Image
        if (image != null) ifNotNull(image.getUser_image(), d::setUser_image);

        // Ben ID
        if (benID != null) d.setBenficieryid(benID.longValue());

        // Related beneficiary IDs — split CSV string into Long[]
        if (d.getRelatedBeneficiaryIdsDB() != null) {
            String[] parts = d.getRelatedBeneficiaryIdsDB().split(",");
            Long[] ids = new Long[parts.length];
            for (int i = 0; i < parts.length; i++)
                ids[i] = Long.valueOf(parts[i].trim());
            d.setRelatedBeneficiaryIds(ids);
        }
    }

// ---------------------------------------------------------------
// EXTRACTED: age calculation
// ---------------------------------------------------------------

    private void applyAge(RMNCHBeneficiaryDetailsRmnch dest, RMNCHMBeneficiarydetail detail) {
        if (detail.getDob() == null) {
            dest.setAgeFull("");
            dest.setAge(0);
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(detail.getDob());
        LocalDate birth = LocalDate.of(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));

        Period p = Period.between(birth, LocalDate.now());
        int y = p.getYears(), mo = p.getMonths(), d = p.getDays();

        String ageFull;
        int ageVal;
        String ageUnit;

        if (y > 0) {
            ageFull = y + " years - " + mo + " months";
            ageVal  = y;
            ageUnit = y > 1 ? "Years" : "Year";
        } else if (mo > 0) {
            ageFull = mo + " months - " + d + " days";
            ageVal  = mo;
            ageUnit = mo > 1 ? "Months" : "Month";
        } else {
            ageFull = d + " days";
            ageVal  = d;
            ageUnit = d > 1 ? "Days" : "Day";
        }

        dest.setAgeFull(ageFull);
        dest.setAge(ageVal);
        dest.setAge_unit(ageUnit);
    }

// ---------------------------------------------------------------
// UTILITY: replaces 50+ repetitive if-not-null setter blocks
// ---------------------------------------------------------------

    private <T> void ifNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

	private Map<String, Object> getBenHealthDetails(BigInteger benRegId) {
		Map<String, Object> healthDetails = new HashMap<>();
		if (null != benRegId) {
			Object[] benHealthIdNumber = beneficiaryRepo.getBenHealthIdNumber(benRegId);
			if (benHealthIdNumber != null && benHealthIdNumber.length > 0) {
				Object[] healthData = (Object[]) benHealthIdNumber[0];
				String healthIdNumber = healthData[0] != null ? healthData[0].toString() : null;
				String healthId = healthData[1] != null ? healthData[1].toString() : null;

				if (null != healthIdNumber) {
					List<Object[]> health = beneficiaryRepo.getBenHealthDetails(healthIdNumber);
					if (health != null && !health.isEmpty()) {
						for (Object[] objects : health) {
							healthDetails.put("HealthID", objects[0]);
							healthDetails.put("HealthIdNumber", objects[1]);
							healthDetails.put("isNewAbha", objects[2]);
						}
					} else {
						healthDetails.put("HealthIdNumber", healthIdNumber);
						healthDetails.put("HealthID", healthId);
						healthDetails.put("isNewAbha", null);
					}
				}
			}
		}
		return healthDetails;
	}

    private Map<String, Object> getBenBenVisitDetails(BigInteger benRegId) {
        Map<String, Object> healthDetails = new HashMap<>();
        if (null != benRegId) {
            String benHealthIdNumber = String.valueOf(beneficiaryRepo.getBenHealthIdNumber(benRegId));
            if (null != benHealthIdNumber) {
                ArrayList<Object[]> health = beneficiaryRepo.getBenHealthDetails(benHealthIdNumber);
                for (Object[] objects : health) {
                    healthDetails.put("HealthID", objects[0]);
                    healthDetails.put("HealthIdNumber", objects[1]);
                    healthDetails.put("isNewAbha", objects[2]);
                }
            }
        }
        return healthDetails;
    }

	public void fetchHealthIdByBenRegID(Long benRegID, String authorization, Map<String, Object> resultMap) {
        Map<String, Long> requestMap = new HashMap<String, Long>();
        requestMap.put("beneficiaryRegID", benRegID);
        requestMap.put("beneficiaryID", null);
        JsonParser jsnParser = new JsonParser();
        HttpUtils utils = new HttpUtils();
        List<String> result = null;
        try {
            HashMap<String, Object> header = new HashMap<String, Object>();
            header.put("Authorization", authorization);
            String responseStr = utils.post(fhirUrl + "/"
                    + getHealthID, new Gson().toJson(requestMap), header);
            JsonElement jsnElmnt = jsnParser.parse(responseStr);
            JsonObject jsnOBJ = new JsonObject();
            jsnOBJ = jsnElmnt.getAsJsonObject();
            if (jsnOBJ.get("data") != null && jsnOBJ.get("data").getAsJsonObject().get("BenHealthDetails") != null) {
                result = new ArrayList<String>();
                BenHealthIDDetails[] ben = InputMapper.gson().fromJson(
                        new Gson().toJson(jsnOBJ.get("data").getAsJsonObject().get("BenHealthDetails")),
                        BenHealthIDDetails[].class);
                for (BenHealthIDDetails value : ben) {
                    if (value.getHealthId() != null)
                        resultMap.put("healthId", value.getHealthId());
                    if (value.getHealthIdNumber() != null)
                        resultMap.put("healthIdNumber", value.getHealthIdNumber());
                }
            }

        } catch (Exception e) {
            logger.info("Error while fetching ABHA" + e.getMessage());
//			return null;
        }

//		return result;

    }


    @Override
    public String saveEyeCheckupVsit(List<EyeCheckupRequestDTO> eyeCheckupRequestDTOS) {

        try {
            for (EyeCheckupRequestDTO dto : eyeCheckupRequestDTOS) {
                EyeCheckupVisit visit = new EyeCheckupVisit();

                visit.setBeneficiaryId(dto.getBeneficiaryId());
                visit.setHouseholdId(dto.getHouseHoldId());
                visit.setUserId(userRepo.getUserIdByName(jwtUtil.getUserNameFromStorage())); // cache se lena hai
                visit.setCreatedBy(dto.getUserName());
                StringBuilder sb = new StringBuilder();


                // fields mapping
                EyeCheckupListDTO f = dto.getFields();
                sb.append(f.getDischarge_summary_upload());
                String longText = sb.toString();
                visit.setVisitDate(LocalDate.parse(f.getVisit_date(), FORMATTER));
                visit.setSymptomsObserved(f.getSymptoms_observed());
                visit.setEyeAffected(f.getEye_affected());
                visit.setReferredTo(f.getReferred_to());
                visit.setDischargeSummaryUpload(longText);
                visit.setFollowUpStatus(f.getFollow_up_status());
                visit.setDateOfSurgery(f.getDate_of_surgery());

                // save/update
                eyeCheckUpVisitRepo.save(visit);
            }
            return "Eye checkup data saved successfully.";
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null ;
    }

    @Override
    public List<EyeCheckupRequestDTO> getEyeCheckUpVisit(GetBenRequestHandler request) {
        List<EyeCheckupVisit> visits = eyeCheckUpVisitRepo.findByUserId(request.getAshaId());

        return visits.stream().map(v -> {
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
        }).collect(Collectors.toList());
    }


}
