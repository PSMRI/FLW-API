package com.iemr.flw.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.iemr.flw.domain.iemr.EyeCheckupVisit;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.dto.iemr.EyeCheckupListDTO;
import com.iemr.flw.dto.iemr.EyeCheckupRequestDTO;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.domain.iemr.BenFlowStatus;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.IncentiveLogicService;
import com.iemr.flw.service.UserService;
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
import com.iemr.flw.domain.iemr.BenAnthropometryDetail;
import com.iemr.flw.domain.iemr.BenPhysicalVitalDetail;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.identity.HouseHoldRepo;
import com.iemr.flw.repo.iemr.BenAnthropometryRepo;
import com.iemr.flw.repo.iemr.BenPhysicalVitalRepo;
import com.iemr.flw.service.BeneficiaryService;
import com.iemr.flw.utils.config.ConfigProperties;
import com.iemr.flw.utils.http.HttpUtils;
import org.springframework.transaction.annotation.Transactional;

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
    private UserService userService;

    @Autowired
    private IncentiveLogicService incentiveLogicService;

    @Autowired
    private BenFlowStatusRepo benFlowStatusRepo;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private BenAnthropometryRepo benAnthropometryRepo;

    @Autowired
    private BenPhysicalVitalRepo benPhysicalVitalRepo;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    @Override
    public String getBenData(GetBenRequestHandler request, String authorisation) throws Exception {

        if (request == null) {
            throw new Exception("Invalid request");
        }

        if (request.getPageNo() == null || request.getPageNo() < 0) {
            throw new Exception("Invalid page number");
        }

        int pageSize = Integer.parseInt(door_to_door_page_size);

        // Stop TB path: filter by providerServiceMapID + villageID
        if (request.getProviderServiceMapID() != null && request.getVillageID() != null) {
            List<BenFlowStatus> flows = benFlowStatusRepo.getRegistrarWorklist(
                    request.getProviderServiceMapID(), request.getVillageID());

            if (flows == null || flows.isEmpty()) return null;

            List<RMNCHMBeneficiaryaddress> allAddresses = new ArrayList<>();
            for (BenFlowStatus flow : flows) {
                if (flow.getBeneficiaryRegID() == null) continue;
                List<RMNCHMBeneficiarymapping> mappings = beneficiaryRepo.findByBenRegIdFromMapping(
                        BigInteger.valueOf(flow.getBeneficiaryRegID()));
                if (mappings.isEmpty()) continue;
                RMNCHMBeneficiarymapping mapping = mappings.get(0);
                if (mapping.getBenAddressId() == null) continue;
                RMNCHMBeneficiaryaddress address = beneficiaryRepo.getAddressById(mapping.getBenAddressId());
                if (address != null) allAddresses.add(address);
            }

            if (allAddresses.isEmpty()) return null;

            int totalPage = (int) Math.ceil((double) allAddresses.size() / pageSize);
            int start = request.getPageNo() * pageSize;
            int end = Math.min(start + pageSize, allAddresses.size());
            if (start >= allAddresses.size()) return null;

            return getMappingsForAddressIDs(allAddresses.subList(start, end), totalPage, authorisation);
        }

        // Normal FLW/ASHA path
        if (request.getAshaId() == null) {
            throw new Exception("Invalid/missing asha details");
        }

        String userName = beneficiaryRepo.getUserName(request.getAshaId());

        if (userName == null || userName.isEmpty()) {
            throw new Exception("Asha details not found, please contact administrator");
        }

        request.setUserName(userName);

        PageRequest pageRequest = PageRequest.of(request.getPageNo(), pageSize);

        Page<RMNCHMBeneficiaryaddress> pageResult;

        if (request.getFromDate() != null && request.getToDate() != null) {

            if (request.getFromDate().after(request.getToDate())) {
                throw new Exception("Invalid date range");
            }

            pageResult = beneficiaryRepo.getBenDataWithinDates(
                    userName,
                    request.getFromDate(),
                    request.getToDate(),
                    pageRequest
            );

        } else {
            pageResult = beneficiaryRepo.getBenDataByUser(userName, pageRequest);
        }

        if (!pageResult.hasContent()) {
            return null;
        }

        return getMappingsForAddressIDs(
                pageResult.getContent(),
                pageResult.getTotalPages(),
                authorisation
        );
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
                if(!beneficiaryRepo.getByAddressID(a.getId()).isEmpty()){
                    RMNCHMBeneficiarymapping m = beneficiaryRepo.getByAddressID(a.getId()).get(0);
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
                            if(!beneficiaryRepo
                                    .getDetailsByRegID((m.getBenRegId()).longValue()).isEmpty()){
                                benDetailsRMNCH_OBJ = beneficiaryRepo
                                        .getDetailsByRegID((m.getBenRegId()).longValue()).get(0);
                            }
                            benBotnBirthRMNCH_ROBJ = beneficiaryRepo.getBornBirthByRegID((m.getBenRegId()).longValue());

                            if (benDetailsRMNCH_OBJ != null && benDetailsRMNCH_OBJ.getHouseoldId() != null)
                                if(!houseHoldRepo
                                        .getByHouseHoldID(benDetailsRMNCH_OBJ.getHouseoldId()).isEmpty()){
                                    benHouseHoldRMNCH_ROBJ = houseHoldRepo
                                            .getByHouseHoldID(benDetailsRMNCH_OBJ.getHouseoldId()).get(0);
                                }


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
                        if (benAddressOBJ.getPermPinCode() != null)
                            benDetailsRMNCH_OBJ.setPinCode(benAddressOBJ.getPermPinCode());

                        // GPS fallback: if not in RMNCH details (syncdatatoamrti not yet called),
                        // pull from i_beneficiaryaddress (saved during TM-API registration)
                        if (benDetailsRMNCH_OBJ.getGpsLatitude() == null && benAddressOBJ.getGpsLatitude() != null)
                            benDetailsRMNCH_OBJ.setGpsLatitude(benAddressOBJ.getGpsLatitude());
                        if (benDetailsRMNCH_OBJ.getGpsLongitude() == null && benAddressOBJ.getGpsLongitude() != null)
                            benDetailsRMNCH_OBJ.setGpsLongitude(benAddressOBJ.getGpsLongitude());
                        if (benDetailsRMNCH_OBJ.getDigipin() == null && benAddressOBJ.getDigipin() != null)
                            benDetailsRMNCH_OBJ.setDigipin(benAddressOBJ.getDigipin());
                        if (benDetailsRMNCH_OBJ.getGpsTimestamp() == null && benAddressOBJ.getGpsTimestamp() != null)
                            benDetailsRMNCH_OBJ.setGpsTimestamp(benAddressOBJ.getGpsTimestamp());
                        if (benDetailsRMNCH_OBJ.getIsGpsUnavailable() == null && benAddressOBJ.getIsGpsUnavailable() != null)
                            benDetailsRMNCH_OBJ.setIsGpsUnavailable(benAddressOBJ.getIsGpsUnavailable());
                        if (benDetailsRMNCH_OBJ.getGpsUnavailableReason() == null && benAddressOBJ.getGpsUnavailableReason() != null)
                            benDetailsRMNCH_OBJ.setGpsUnavailableReason(benAddressOBJ.getGpsUnavailableReason());

                        // Map GPS double fields to the exposed latitude/longitude BigDecimal fields for response
                        if (benDetailsRMNCH_OBJ.getGpsLatitude() != null)
                            benDetailsRMNCH_OBJ.setLatitude(BigDecimal.valueOf(benDetailsRMNCH_OBJ.getGpsLatitude()));
                        if (benDetailsRMNCH_OBJ.getGpsLongitude() != null)
                            benDetailsRMNCH_OBJ.setLongitude(BigDecimal.valueOf(benDetailsRMNCH_OBJ.getGpsLongitude()));

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

                        if (benDetailsOBJ.getDob() != null) {
                            benDetailsRMNCH_OBJ.setDob(benDetailsOBJ.getDob());
                        } else {
                            // i_beneficiarydetails.dob is null for Stop TB mobile registrations
                            // (Identity-API mapper commented out) — fall back to i_ben_flow_outreach.ben_dob
                            List<BenFlowStatus> flows = benFlowStatusRepo.findByBeneficiaryRegID(m.getBenRegId().longValue());
                            if (!flows.isEmpty() && flows.get(0).getDob() != null)
                                benDetailsRMNCH_OBJ.setDob(flows.get(0).getDob());
                        }
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
                        if (benDetailsRMNCH_OBJ.getDob() != null) {

                            Date date = new Date(benDetailsRMNCH_OBJ.getDob().getTime());
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
                        resultMap.put("isDeactivate", benDetailsRMNCH_OBJ.getIsDeactivate() != null
                                ? benDetailsRMNCH_OBJ.getIsDeactivate()
                                : false
                        );
                        resultMap.put("economicStatus",benDetailsOBJ.getEconomicStatus());
                        resultMap.put("economicStatusId",benDetailsOBJ.getEconomicStatusId());
                        resultMap.put("residentialAreaId",benDetailsOBJ.getResidentialAreaId());
                        resultMap.put("residentialArea",benDetailsOBJ.getResidentialArea());
                        resultMap.put("address",benDetailsOBJ.getAddress());
                        resultMap.put("updateDate", benDetailsOBJ.getLastModDate());
                        resultMap.put("updatedBy", benDetailsOBJ.getModifiedBy());
                        resultMap.put("BenRegId", m.getBenRegId());

                        // occupation from i_beneficiarydetails
                        if (benDetailsOBJ != null && benDetailsOBJ.getOccupation() != null)
                            benDetailsRMNCH_OBJ.setOccupation(benDetailsOBJ.getOccupation());

                        // anthropometry from t_phy_anthropometry, vitals from t_phy_vitals
                        // fallback to otherFields if exam not yet saved for this beneficiary
                        Long benRegIdLong = m.getBenRegId() != null ? m.getBenRegId().longValue() : null;
                        if (benRegIdLong != null) {
                            List<BenAnthropometryDetail> anthroList =
                                    benAnthropometryRepo.findByBeneficiaryRegIDOrderByCreatedDateDesc(benRegIdLong);
                            List<BenPhysicalVitalDetail> vitalList =
                                    benPhysicalVitalRepo.findByBeneficiaryRegIDOrderByCreatedDateDesc(benRegIdLong);

                            Map<String, Object> anthropometry = new HashMap<>();
                            if (!anthroList.isEmpty()) {
                                BenAnthropometryDetail anthro = anthroList.get(0);
                                if (anthro.getHeightCm() != null) anthropometry.put("height", anthro.getHeightCm());
                                if (anthro.getWeightKg() != null) anthropometry.put("weight", anthro.getWeightKg());
                                if (anthro.getBmi()      != null) anthropometry.put("bmi",    anthro.getBmi());
                            }
                            if (!vitalList.isEmpty()) {
                                BenPhysicalVitalDetail vital = vitalList.get(0);
                                if (vital.getTemperature()        != null) anthropometry.put("temperatureValue",    vital.getTemperature());
                                if (vital.getPulseRate()          != null) anthropometry.put("pulseRate",           vital.getPulseRate());
                                if (vital.getSystolicBP()         != null) anthropometry.put("systolicBP",          vital.getSystolicBP());
                                if (vital.getDiastolicBP()        != null) anthropometry.put("diastolicBP",         vital.getDiastolicBP());
                                if (vital.getBloodGlucoseRandom() != null) anthropometry.put("bloodGlucoseRandom",  vital.getBloodGlucoseRandom());
                            }

                            // fallback: if no exam saved yet, read from registration otherFields
                            if (anthropometry.isEmpty() && benDetailsOBJ != null && benDetailsOBJ.getOtherFields() != null) {
                                try {
                                    Map<?, ?> extraFields = new Gson().fromJson(benDetailsOBJ.getOtherFields(), Map.class);
                                    if (extraFields.containsKey("weight")) anthropometry.put("weight", extraFields.get("weight"));
                                    if (extraFields.containsKey("height")) anthropometry.put("height", extraFields.get("height"));
                                    if (extraFields.containsKey("bmi"))    anthropometry.put("bmi",    extraFields.get("bmi"));
                                    if (extraFields.containsKey("temperatureValue")) anthropometry.put("temperatureValue", extraFields.get("temperatureValue"));
                                } catch (Exception ex) {
                                    logger.warn("Could not parse otherFields for fallback anthropometry: " + benDetailsOBJ.getBeneficiaryDetailsId());
                                }
                            }
                            if (!anthropometry.isEmpty()) resultMap.put("anthropometry", anthropometry);
                        }

                        // Stop TB fields from ExtraFields
                        Map<String, Object> stopTBDetails = new HashMap<>();
                        if (benDetailsOBJ != null && benDetailsOBJ.getOtherFields() != null) {
                            try {
                                Map<?, ?> extraFields = new Gson().fromJson(benDetailsOBJ.getOtherFields(), Map.class);

                                if (extraFields.containsKey("personFrom")) stopTBDetails.put("personFrom", extraFields.get("personFrom"));
                                if (extraFields.containsKey("caseFindingType")) stopTBDetails.put("caseFindingType", extraFields.get("caseFindingType"));
                                if (extraFields.containsKey("isMobileAvailable")) stopTBDetails.put("isMobileAvailable", extraFields.get("isMobileAvailable"));
                                if (extraFields.containsKey("tuId")) stopTBDetails.put("tuId", extraFields.get("tuId"));
                                if (extraFields.containsKey("tuName")) stopTBDetails.put("tuName", extraFields.get("tuName"));

                                // economicStatus and residentialArea into beneficiaryDetails
                                if (extraFields.containsKey("economicStatus")) benDetailsRMNCH_OBJ.setEconomicStatus((String) extraFields.get("economicStatus"));
                                if (extraFields.containsKey("economicStatusId")) benDetailsRMNCH_OBJ.setEconomicStatusId(((Number) extraFields.get("economicStatusId")).intValue());
                                if (extraFields.containsKey("residentialArea")) benDetailsRMNCH_OBJ.setResidentialArea((String) extraFields.get("residentialArea"));
                                if (extraFields.containsKey("residentialAreaId")) benDetailsRMNCH_OBJ.setResidentialAreaId(((Number) extraFields.get("residentialAreaId")).intValue());
                            } catch (Exception ex) {
                                logger.warn("Could not parse ExtraFields for benDetailsId: " + benDetailsOBJ.getBeneficiaryDetailsId());
                            }
                        }
                        if (!stopTBDetails.isEmpty()) resultMap.put("stopTBDetails", stopTBDetails);

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
                }

            } catch (Exception e) {
                logger.info("Error for ben :"+e.getMessage());
                logger.info("Error for ben :"+e);
                logger.error("error for addressID :" + e.getMessage() + a.getId() + " and vanID : " + a.getVanID());
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
    public String saveEyeCheckupVsit(List<EyeCheckupRequestDTO> eyeCheckupRequestDTOS, String token) {

        try {
            for (EyeCheckupRequestDTO dto : eyeCheckupRequestDTOS) {
                EyeCheckupVisit visit = new EyeCheckupVisit();

                visit.setBeneficiaryId(dto.getBeneficiaryId());
                visit.setHouseholdId(dto.getHouseHoldId());
                visit.setUserId(jwtUtil.extractUserId(token));
                visit.setCreatedBy(dto.getUserName());

                EyeCheckupListDTO f = dto.getFields();

                visit.setSymptomsObserved(f.getSymptomsAsString());

                String upload = f.getDischarge_summary_upload();
                visit.setDischargeSummaryUpload(
                        (upload != null && !upload.equalsIgnoreCase("null")) ? upload : null
                );

                visit.setVisitDate(LocalDate.parse(f.getVisit_date(), FORMATTER));

                visit.setDateOfSurgery(f.getDate_of_surgery());


                visit.setEyeAffected(f.getEye_affected());
                visit.setReferredTo(f.getReferred_to());
                visit.setFollowUpStatus(f.getFollow_up_status());

                eyeCheckUpVisitRepo.save(visit);
                if (visit.getReferredTo() != null) {
                    if (visit.getReferredTo().equals("Govt Public Facility")) {
                        LocalDate localDate = visit.getVisitDate();

                        Timestamp visitDate = Timestamp.valueOf(localDate.atStartOfDay());
                        incentiveLogicService.incentiveForEyeSurgeyReferGovtHospital(visit.getBeneficiaryId(), visitDate, visitDate, visit.getUserId());
                    }

                    if (visit.getReferredTo().equals("Private Facility")) {
                        LocalDate localDate = visit.getVisitDate();

                        Timestamp visitDate = Timestamp.valueOf(localDate.atStartOfDay());
                        incentiveLogicService.incentiveForEyeSurgeyReferPrivateHospital(visit.getBeneficiaryId(), visitDate, visitDate, visit.getUserId());
                    }
                }

            }

            return "Eye checkup data saved successfully.";

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid date format. Expected dd-MM-yyyy. " + e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save eye checkup data: " + e.getMessage());
        }
    }

    @Override
    public List<EyeCheckupRequestDTO> getEyeCheckUpVisit(GetBenRequestHandler request,String token) {
        String createdBy = null;
        try {
            createdBy = userService.getUserDetail(jwtUtil.extractUserId(token)).getUserName();
        } catch (Exception e) {
            logger.error("Error extracting userId from token: " + e.getMessage());
        }
        List<EyeCheckupVisit> visits = eyeCheckUpVisitRepo.findByCreatedBy(createdBy);

        return visits.stream().map(v -> {
            EyeCheckupRequestDTO dto = new EyeCheckupRequestDTO();
            dto.setId(v.getId());
            dto.setEyeSide(v.getEyeAffected());
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
            if (v.getReferredTo() != null) {
                if (v.getReferredTo().equals("Govt Public Facility")) {
                    LocalDate localDate = v.getVisitDate();

                    Timestamp visitDate = Timestamp.valueOf(localDate.atStartOfDay());
                    incentiveLogicService.incentiveForEyeSurgeyReferGovtHospital(v.getBeneficiaryId(), visitDate, visitDate, v.getUserId());
                }

                if (v.getReferredTo().equals("Private Facility")) {
                    LocalDate localDate = v.getVisitDate();

                    Timestamp visitDate = Timestamp.valueOf(localDate.atStartOfDay());
                    incentiveLogicService.incentiveForEyeSurgeyReferPrivateHospital(v.getBeneficiaryId(), visitDate, visitDate, v.getUserId());
                }
            }

            return dto;
        }).collect(Collectors.toList());
    }


}
