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
        String outputResponse = null;
        int totalPage = 0;

        try {
            if (request != null && request.getAshaId() != null) {
                List<RMNCHMBeneficiaryaddress> resultSet;
                Integer pageSize = Integer.valueOf(door_to_door_page_size);
                if (request.getPageNo() != null) {
                    String userName = beneficiaryRepo.getUserName(request.getAshaId());
                    if (userName == null || userName.isEmpty())
                        throw new Exception("Asha details not found, please contact administrator");

                    request.setUserName(userName);

                    PageRequest pr = PageRequest.of(request.getPageNo(), pageSize);
                    if (request.getFromDate() != null && request.getToDate() != null) {
                        Page<RMNCHMBeneficiaryaddress> p = beneficiaryRepo.getBenDataWithinDates(
                                request.getUserName(), request.getFromDate(), request.getToDate(), pr);
                        resultSet = p.getContent();
                        totalPage = p.getTotalPages();
                    } else {
                        Page<RMNCHMBeneficiaryaddress> p = beneficiaryRepo.getBenDataByUser(request.getUserName(),
                                pr);
                        resultSet = p.getContent();
                        totalPage = p.getTotalPages();
                    }
                    if (resultSet != null && resultSet.size() > 0) {
                        outputResponse = getMappingsForAddressIDs(resultSet, totalPage, authorisation);
                    }
                } else {
                    // page no not invalid
                    throw new Exception("Invalid page no");
                }
            } else
                throw new Exception("Invalid/missing village details");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return outputResponse;
    }

    private String getMappingsForAddressIDs(List<RMNCHMBeneficiaryaddress> addressList, int totalPage,
                                            String authorisation) {
        RMNCHHouseHoldDetails benHouseHoldRMNCH_ROBJ;
        RMNCHBeneficiaryDetailsRmnch benDetailsRMNCH_OBJ;
        RMNCHBornBirthDetails benBotnBirthRMNCH_ROBJ;

        RMNCHMBeneficiarydetail benDetailsOBJ;
        RMNCHMBeneficiaryAccount benAccountOBJ;
        RMNCHMBeneficiaryImage benImageOBJ;
        RMNCHMBeneficiaryaddress benAddressOBJ;
        RMNCHMBeneficiarycontact benContactOBJ;

        Map<String, Object> resultMap;
        ArrayList<Map<String, Object>> resultList = new ArrayList<>();

        for (RMNCHMBeneficiaryaddress a : addressList) {
            // exception by-passing
            try {
                RMNCHMBeneficiarymapping m = beneficiaryRepo.getByAddressID(a.getId());
                if (m != null) {
                    benHouseHoldRMNCH_ROBJ = new RMNCHHouseHoldDetails();
                    benDetailsRMNCH_OBJ = new RMNCHBeneficiaryDetailsRmnch();
                    benBotnBirthRMNCH_ROBJ = new RMNCHBornBirthDetails();

                    benDetailsOBJ = new RMNCHMBeneficiarydetail();
                    benAccountOBJ = new RMNCHMBeneficiaryAccount();
                    benImageOBJ = new RMNCHMBeneficiaryImage();
                    benAddressOBJ = new RMNCHMBeneficiaryaddress();
                    benContactOBJ = new RMNCHMBeneficiarycontact();
                    Map<String, Object> healthDetails = getBenHealthDetails(m.getBenRegId());
                    if (m.getBenDetailsId() != null) {
                        benDetailsOBJ = beneficiaryRepo.getDetailsById(m.getBenDetailsId());
                    }
                    if (m.getBenAccountID() != null) {
                        benAccountOBJ = beneficiaryRepo.getAccountById(m.getBenAccountID());
                    }
                    if (m.getBenImageId() != null) {
                        benImageOBJ = beneficiaryRepo.getImageById(m.getBenImageId().longValue());
                    }
                    if (m.getBenAddressId() != null) {
                        benAddressOBJ = beneficiaryRepo.getAddressById(m.getBenAddressId());
                    }
                    if (m.getBenContactsId() != null) {
                        benContactOBJ = beneficiaryRepo.getContactById(m.getBenContactsId());
                    }

                    BigInteger benID = null;
                    if (m.getBenRegId() != null)
                        benID = beneficiaryRepo.getBenIdFromRegID(m.getBenRegId().longValue());

                    if (m.getBenRegId() != null) {
                        benDetailsRMNCH_OBJ = beneficiaryRepo
                                .getDetailsByRegID((m.getBenRegId()).longValue());
                        benBotnBirthRMNCH_ROBJ = beneficiaryRepo.getBornBirthByRegID((m.getBenRegId()).longValue());

                        if (benDetailsRMNCH_OBJ != null && benDetailsRMNCH_OBJ.getHouseoldId() != null)
                            benHouseHoldRMNCH_ROBJ = houseHoldRepo
                                    .getByHouseHoldID(benDetailsRMNCH_OBJ.getHouseoldId());

                    }
                    if (benDetailsRMNCH_OBJ == null)
                        benDetailsRMNCH_OBJ = new RMNCHBeneficiaryDetailsRmnch();

                    // new mapping 30-06-2021
                    if (benDetailsOBJ.getMotherName() != null)
                        benDetailsRMNCH_OBJ.setMotherName(benDetailsOBJ.getMotherName());
                    if (benDetailsOBJ.getLiteracyStatus() != null)
                        benDetailsRMNCH_OBJ.setLiteracyStatus(benDetailsOBJ.getLiteracyStatus());

                    // bank
                    if (benAccountOBJ.getNameOfBank() != null)
                        benDetailsRMNCH_OBJ.setNameOfBank(benAccountOBJ.getNameOfBank());
                    if (benAccountOBJ.getBranchName() != null)
                        benDetailsRMNCH_OBJ.setBranchName(benAccountOBJ.getBranchName());
                    if (benAccountOBJ.getIfscCode() != null)
                        benDetailsRMNCH_OBJ.setIfscCode(benAccountOBJ.getIfscCode());
                    if (benAccountOBJ.getBankAccount() != null)
                        benDetailsRMNCH_OBJ.setBankAccount(benAccountOBJ.getBankAccount());

                    // location
                    if (benAddressOBJ.getCountyid() != null)
                        benDetailsRMNCH_OBJ.setCountryId(benAddressOBJ.getCountyid());
                    if (benAddressOBJ.getPermCountry() != null)
                        benDetailsRMNCH_OBJ.setCountryName(benAddressOBJ.getPermCountry());

                    if (benAddressOBJ.getStatePerm() != null)
                        benDetailsRMNCH_OBJ.setStateId(benAddressOBJ.getStatePerm());
                    if (benAddressOBJ.getPermState() != null)
                        benDetailsRMNCH_OBJ.setStateName(benAddressOBJ.getPermState());

                    if (benAddressOBJ.getDistrictidPerm() != null) {
                        benDetailsRMNCH_OBJ.setDistrictid(benAddressOBJ.getDistrictidPerm());

                    }
                    if (benAddressOBJ.getDistrictnamePerm() != null) {
                        benDetailsRMNCH_OBJ.setDistrictname(benAddressOBJ.getDistrictnamePerm());

                    }

                    if (benAddressOBJ.getPermSubDistrictId() != null)
                        benDetailsRMNCH_OBJ.setBlockId(benAddressOBJ.getPermSubDistrictId());
                    if (benAddressOBJ.getPermSubDistrict() != null)
                        benDetailsRMNCH_OBJ.setBlockName(benAddressOBJ.getPermSubDistrict());

                    if (benAddressOBJ.getVillageidPerm() != null)
                        benDetailsRMNCH_OBJ.setVillageId(benAddressOBJ.getVillageidPerm());
                    if (benAddressOBJ.getVillagenamePerm() != null)
                        benDetailsRMNCH_OBJ.setVillageName(benAddressOBJ.getVillagenamePerm());

                    if (benAddressOBJ.getPermServicePointId() != null)
                        benDetailsRMNCH_OBJ.setServicePointID(benAddressOBJ.getPermServicePointId());
                    if (benAddressOBJ.getPermServicePoint() != null)
                        benDetailsRMNCH_OBJ.setServicePointName(benAddressOBJ.getPermServicePoint());

                    if (benAddressOBJ.getPermZoneID() != null)
                        benDetailsRMNCH_OBJ.setZoneID(benAddressOBJ.getPermZoneID());
                    if (benAddressOBJ.getPermZone() != null)
                        benDetailsRMNCH_OBJ.setZoneName(benAddressOBJ.getPermZone());

                    if (benAddressOBJ.getPermAddrLine1() != null)
                        benDetailsRMNCH_OBJ.setAddressLine1(benAddressOBJ.getPermAddrLine1());
                    if (benAddressOBJ.getPermAddrLine2() != null)
                        benDetailsRMNCH_OBJ.setAddressLine2(benAddressOBJ.getPermAddrLine2());
                    if (benAddressOBJ.getPermAddrLine3() != null)
                        benDetailsRMNCH_OBJ.setAddressLine3(benAddressOBJ.getPermAddrLine3());

                    // -----------------------------------------------------------------------------

                    // related benids
                    if (benDetailsRMNCH_OBJ.getRelatedBeneficiaryIdsDB() != null) {

                        String[] relatedBenIDsString = benDetailsRMNCH_OBJ.getRelatedBeneficiaryIdsDB().split(",");
                        Long[] relatedBenIDs = new Long[relatedBenIDsString.length];
                        int pointer = 0;
                        for (String s : relatedBenIDsString) {
                            relatedBenIDs[pointer] = Long.valueOf(s);
                            pointer++;
                        }

                        benDetailsRMNCH_OBJ.setRelatedBeneficiaryIds(relatedBenIDs);
                    }
                    // ------------------------------------------------------------------------------

                    if (benDetailsOBJ.getCommunity() != null)
                        benDetailsRMNCH_OBJ.setCommunity(benDetailsOBJ.getCommunity());
                    if (benDetailsOBJ.getCommunityId() != null)
                        benDetailsRMNCH_OBJ.setCommunityId(benDetailsOBJ.getCommunityId());
                    if (benContactOBJ.getPreferredPhoneNum() != null)
                        benDetailsRMNCH_OBJ.setContact_number(benContactOBJ.getPreferredPhoneNum());

                    if (benDetailsOBJ.getDob() != null)
                        benDetailsRMNCH_OBJ.setDob(benDetailsOBJ.getDob());
                    if (benDetailsOBJ.getFatherName() != null)
                        benDetailsRMNCH_OBJ.setFatherName(benDetailsOBJ.getFatherName());
                    if (benDetailsOBJ.getFirstName() != null)
                        benDetailsRMNCH_OBJ.setFirstName(benDetailsOBJ.getFirstName());
                    if (benDetailsOBJ.getGender() != null)
                        benDetailsRMNCH_OBJ.setGender(benDetailsOBJ.getGender());
                    if (benDetailsOBJ.getGenderId() != null)
                        benDetailsRMNCH_OBJ.setGenderId(benDetailsOBJ.getGenderId());

                    if (benDetailsOBJ.getMaritalstatus() != null)
                        benDetailsRMNCH_OBJ.setMaritalstatus(benDetailsOBJ.getMaritalstatus());
                    if (benDetailsOBJ.getMaritalstatusId() != null)
                        benDetailsRMNCH_OBJ.setMaritalstatusId(benDetailsOBJ.getMaritalstatusId());
                    if (benDetailsOBJ.getMarriageDate() != null)
                        benDetailsRMNCH_OBJ.setMarriageDate(benDetailsOBJ.getMarriageDate());

                    if (benDetailsOBJ.getReligion() != null)
                        benDetailsRMNCH_OBJ.setReligion(benDetailsOBJ.getReligion());
                    if (benDetailsOBJ.getReligionID() != null)
                        benDetailsRMNCH_OBJ.setReligionID(benDetailsOBJ.getReligionID());
                    if (benDetailsOBJ.getSpousename() != null)
                        benDetailsRMNCH_OBJ.setSpousename(benDetailsOBJ.getSpousename());

                    if (benImageOBJ != null && benImageOBJ.getUser_image() != null)
                        benDetailsRMNCH_OBJ.setUser_image(benImageOBJ.getUser_image());

                    // new fields
//                    benDetailsRMNCH_OBJ.setRegistrationDate(benDetailsOBJ.getCreatedDate());
                    if (benID != null)
                        benDetailsRMNCH_OBJ.setBenficieryid(benID.longValue());

                    if (benDetailsOBJ.getLastName() != null)
                        benDetailsRMNCH_OBJ.setLastName(benDetailsOBJ.getLastName());

                    if (benDetailsRMNCH_OBJ.getCreatedBy() == null)
                        if (benDetailsOBJ.getCreatedBy() != null)
                            benDetailsRMNCH_OBJ.setCreatedBy(benDetailsOBJ.getCreatedBy());

                    // age calculation
                    String ageDetails = "";
                    int age_val = 0;
                    String ageUnit = null;
                    if (benDetailsOBJ.getDob() != null) {

                        Date date = new Date(benDetailsOBJ.getDob().getTime());
                        Calendar cal = Calendar.getInstance();

                        cal.setTime(date);

                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH) + 1;
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        java.time.LocalDate todayDate = java.time.LocalDate.now();
                        java.time.LocalDate birthdate = java.time.LocalDate.of(year, month, day);
                        Period p = Period.between(birthdate, todayDate);

                        int d = p.getDays();
                        int mo = p.getMonths();
                        int y = p.getYears();

                        if (y > 0) {
                            ageDetails = y + " years - " + mo + " months";
                            age_val = y;
                            ageUnit = (age_val > 1) ? "Years" : "Year";
                        } else {
                            if (mo > 0) {
                                ageDetails = mo + " months - " + d + " days";
                                age_val = mo;
                                ageUnit = (age_val > 1) ? "Months" : "Month";
                            } else {
                                ageDetails = d + " days";
                                age_val = d;
                                ageUnit = (age_val > 1) ? "Days" : "Day";
                            }
                        }

                    }

                    benDetailsRMNCH_OBJ.setAgeFull(ageDetails);
                    benDetailsRMNCH_OBJ.setAge(age_val);
                    if (ageUnit != null)
                        benDetailsRMNCH_OBJ.setAge_unit(ageUnit);

                    resultMap = new HashMap<>();
                    if (benHouseHoldRMNCH_ROBJ != null)
                        resultMap.put("householdDetails", benHouseHoldRMNCH_ROBJ);
                    else
                        resultMap.put("householdDetails", new HashMap<String, Object>());

                    if (benBotnBirthRMNCH_ROBJ != null)
                        resultMap.put("bornbirthDeatils", benBotnBirthRMNCH_ROBJ);
                    else
                        resultMap.put("bornbirthDeatils", new HashMap<String, Object>());

                    resultMap.put("beneficiaryDetails", benDetailsRMNCH_OBJ);
                    resultMap.put("abhaHealthDetails", healthDetails);
                    resultMap.put("houseoldId", benDetailsRMNCH_OBJ.getHouseoldId());
                    resultMap.put("benficieryid", benDetailsRMNCH_OBJ.getBenficieryid());
                    resultMap.put("isDeath", benDetailsRMNCH_OBJ.getIsDeath());
                    resultMap.put("isDeathValue", benDetailsRMNCH_OBJ.getIsDeathValue());
                    resultMap.put("dateOfDeath",benDetailsRMNCH_OBJ.getDateOfDeath());
                    resultMap.put("timeOfDeath", benDetailsRMNCH_OBJ.getTimeOfDeath());
                    resultMap.put("reasonOfDeath", benDetailsRMNCH_OBJ.getReasonOfDeath());
                    resultMap.put("reasonOfDeathId", benDetailsRMNCH_OBJ.getReasonOfDeathId());
                    resultMap.put("placeOfDeath", benDetailsRMNCH_OBJ.getPlaceOfDeath());
                    resultMap.put("placeOfDeathId", benDetailsRMNCH_OBJ.getPlaceOfDeathId());
                    resultMap.put("isSpouseAdded", benDetailsRMNCH_OBJ.getIsSpouseAdded());
                    resultMap.put("isChildrenAdded", benDetailsRMNCH_OBJ.getIsChildrenAdded());
                    resultMap.put("noOfchildren", benDetailsRMNCH_OBJ.getNoOfchildren());
                    resultMap.put("isMarried", benDetailsRMNCH_OBJ.getIsMarried());
                    resultMap.put("doYouHavechildren", benDetailsRMNCH_OBJ.getDoYouHavechildren());
                    resultMap.put("noofAlivechildren",benDetailsRMNCH_OBJ.getNoofAlivechildren());
                    resultMap.put("isDeactivate",benDetailsRMNCH_OBJ.getIsDeactivate());
                    resultMap.put("BenRegId", m.getBenRegId());

                    // adding asha id / created by - user id
                    if (benAddressOBJ.getCreatedBy() != null) {
                        Integer userID = beneficiaryRepo.getUserIDByUserName(benAddressOBJ.getCreatedBy());
                        if (userID != null && userID > 0)
                            resultMap.put("ashaId", userID);
                    }
                    // get HealthID of ben
                    if (m.getBenRegId() != null) {
                        fetchHealthIdByBenRegID(m.getBenRegId().longValue(), authorisation, resultMap);
                    }

                    resultList.add(resultMap);

                } else {
                    // mapping not available
                }
            } catch (Exception e) {
                logger.error("error for addressID :"+e.getMessage() + a.getId() + " and vanID : " + a.getVanID());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", resultList);
        response.put("pageSize", Integer.parseInt(door_to_door_page_size));
        response.put("totalPage", totalPage);
        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
        return gson.toJson(response);
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
