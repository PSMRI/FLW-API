package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.identity.RMNCHBeneficiaryDetailsRmnch;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.CdrDTO;
import com.iemr.flw.dto.iemr.MdsrDTO;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.DeathReportsService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeathReportsServiceImpl implements DeathReportsService {

    @Autowired
    private CdrRepo cdrRepo;

    @Autowired
    private MdsrRepo mdsrRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    ObjectMapper mapper = new ObjectMapper();

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;

    @Autowired
    private UpdateIncentivePendindDocService pendindDocService;

    private final Logger logger = LoggerFactory.getLogger(DeathReportsServiceImpl.class);

    @Override
    public String registerCDR(List<CdrDTO> cdrDTOs) {
        try {
            List<CDR> cdrList = new ArrayList<>();
            cdrDTOs.forEach(it -> {
                CDR existingCDR =
                        cdrRepo.findCDRByBenId(it.getBenId());

                if (existingCDR != null) {
                    Long id = existingCDR.getId();
                    modelMapper.map(it, existingCDR);
                    existingCDR.setId(id);
                } else {

                    existingCDR = new CDR();
                    modelMapper.map(it, existingCDR);
                    existingCDR.setId(null);
                }
                cdrList.add(existingCDR);
            });
            cdrRepo.saveAll(cdrList);
            checkAndAddIncentives(cdrList);

            return "no of cdr details saved: " + cdrDTOs.size();
        } catch (Exception e) {
            return "error while saving cdr details: " + e.getMessage();
        }
    }

    public String updateCDRUploadFiles(CdrDTO cdrDTOs, Long incentiveActivityId) {
        try {
            Optional<CDR> cdrOptional = cdrRepo.findById(cdrDTOs.getId());
            CDR cdr = new CDR();
            if (cdrOptional.isPresent()) {
                cdr = cdrOptional.get();
                if (cdrDTOs.getCdrImage() != null && !cdrDTOs.getCdrImage().isEmpty()) {
                    cdr.setCdrImage(cdrDTOs.getCdrImage());
                }

                if (cdrDTOs.getCdrImage2() != null && !cdrDTOs.getCdrImage2().isEmpty()) {
                    cdr.setCdrImage2(cdrDTOs.getCdrImage2());
                }

                if (cdrDTOs.getDeathCertImage1() != null && !cdrDTOs.getDeathCertImage1().isEmpty()) {
                    cdr.setDeathCertImage1(cdrDTOs.getDeathCertImage1());
                }

                if (cdrDTOs.getDeathCertImage2() != null && !cdrDTOs.getDeathCertImage2().isEmpty()) {
                    cdr.setDeathCertImage2(cdrDTOs.getDeathCertImage2());
                }
            }
            cdrRepo.save(cdr);

            pendindDocService.updateIncentive(incentiveActivityId);

            return "no of cdr details update: ";
        } catch (Exception e) {
            return "error while saving cdr details: " + e.getMessage();
        }
    }

    @Override
    public String registerMDSR(List<MdsrDTO> mdsrDTOs) {
        try {
            List<MDSR> mdsrList = new ArrayList<>();
            mdsrDTOs.forEach(it -> {
                MDSR mdsr =
                        mdsrRepo.findMDSRByBenId(it.getBenId());

                if (mdsr != null) {
                    Long id = mdsr.getId();
                    modelMapper.map(it, mdsr);
                    mdsr.setId(id);
                } else {
                    mdsr = new MDSR();
                    modelMapper.map(it, mdsr);
                    mdsr.setId(null);
                }
                mdsrList.add(mdsr);
            });
            mdsrRepo.saveAll(mdsrList);
            checkAndAddIncentivesMdsr(mdsrList);

            return "no of mdsr details saved: " + mdsrDTOs.size();
        } catch (Exception e) {
            return "error while saving mdsr details: " + e.getMessage();
        }
    }

    public String updateMDSR(MdsrDTO mdsrDTO, Long incentiveActivityId) {
        try {

            Optional<MDSR> mdsrOptional = mdsrRepo.findById(mdsrDTO.getId());
            MDSR mdsr = new MDSR();

            if (mdsrOptional.isPresent()) {

                mdsr = mdsrOptional.get();

                if (mdsrDTO.getMdsr1File() != null && !mdsrDTO.getMdsr1File().isEmpty()) {
                    mdsr.setMdsr1File(mdsrDTO.getMdsr1File());
                }

                if (mdsrDTO.getMdsr2File() != null && !mdsrDTO.getMdsr2File().isEmpty()) {
                    mdsr.setMdsr2File(mdsrDTO.getMdsr2File());
                }

                if (mdsrDTO.getMdsrDeathCertFile() != null && !mdsrDTO.getMdsrDeathCertFile().isEmpty()) {
                    mdsr.setMdsrDeathCertFile(mdsrDTO.getMdsrDeathCertFile());
                }

            } else {
                return "MDSR record not found with id: " + mdsrDTO.getId();
            }

            mdsrRepo.save(mdsr);

            // incentive update
            pendindDocService.updateIncentive(incentiveActivityId);

            return "MDSR updated successfully for id: " + mdsrDTO.getId();

        } catch (Exception e) {
            return "error while updating mdsr details: " + e.getMessage();
        }
    }

    @Override
    public List<CdrDTO> getCdrRecords(GetBenRequestHandler dto) {
        try {
            String user = userRepo.getUserNamedByUserId(dto.getAshaId());
            List<CDR> cdrlist =
                    cdrRepo.findByCreatedBy(user);
            return cdrlist.stream()
                    .map(cdr -> mapper.convertValue(cdr, CdrDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("MDSR Exception:" + e.getMessage());

            // log
        }
        return null;
    }

    @Override
    public List<MdsrDTO> getMdsrRecords(GetBenRequestHandler dto) {

        try {
            String user = userRepo.getUserNamedByUserId(dto.getAshaId());

            List<MDSR> mdsrList =
                    mdsrRepo.findByCreatedBy(user);
            return mdsrList.stream()
                    .map(mdsr -> mapper.convertValue(mdsr, MdsrDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("MDSR Exception:" + e.getMessage());

            // log
        }
        return null;
    }

    private void checkAndAddIncentives(List<CDR> cdrList) {

        cdrList.forEach(cdr -> {
            Integer userId = userRepo.getUserIdByName(cdr.getCreatedBy());
            IncentiveActivity immunizationActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("CHILD_DEATH_REPORTING", GroupName.CHILD_HEALTH.getDisplayName());
            createIncentiveRecord(cdr, cdr.getBenId(), userId, immunizationActivity);
        });
    }


    private void checkAndAddIncentivesMdsr(List<MDSR> mdsrList) {


        mdsrList.forEach(mdsr -> {
            Integer userId = userRepo.getUserIdByName(mdsr.getCreatedBy());
            IncentiveActivity immunizationActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("MATERNAL_DEATH_REPORT", GroupName.MATERNAL_HEALTH.getDisplayName());

            createIncentiveRecord(mdsr, mdsr.getBenId(), userId, immunizationActivity);
        });
    }


    private void createIncentiveRecord(CDR cdr, Long benId, Integer userId, IncentiveActivity immunizationActivity) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(immunizationActivity.getId(), cdr.getCreatedDate(), benId);
        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(immunizationActivity.getId());
            record.setCreatedDate(cdr.getCreatedDate());
            record.setCreatedBy(cdr.getCreatedBy());
            record.setStartDate(cdr.getCreatedDate());
            record.setEndDate(cdr.getCreatedDate());
            record.setUpdatedDate(cdr.getCreatedDate());
            record.setUpdatedBy(cdr.getCreatedBy());
            record.setBenId(benId);
            record.setAshaId(userId);
            record.setAmount(Long.valueOf(immunizationActivity.getRate()));
            if (userRepo.getUserRole(userId).get(0).getStateId() == StateCode.AM.getStateCode()) {
                if (cdr.getDeathCertImage1() != null && cdr.getCdrImage() != null && cdr.getCdrImage2() != null && cdr.getDeathCertImage2() != null) {
                    record.setIsEligible(true);
                    recordRepo.save(record);

                } else {
                    record.setIsEligible(false);
                    IncentiveActivityRecord incentiveActivityRecord = recordRepo.save(record);
                    if (incentiveActivityRecord == null) {
                        recordRepo.save(record);
                        pendindDocService.updatePendingActivity(userId, cdr.getId(), record.getId(), immunizationActivity.getId());

                    }

                }
            } else {
                recordRepo.save(record);

            }

        }
    }

    private void createIncentiveRecord(MDSR mdsr, Long benId, Integer userId, IncentiveActivity immunizationActivity) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(immunizationActivity.getId(), mdsr.getCreatedDate(), benId);
        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(immunizationActivity.getId());
            record.setCreatedDate(mdsr.getCreatedDate());
            record.setCreatedBy(mdsr.getCreatedBy());
            record.setStartDate(mdsr.getCreatedDate());
            record.setEndDate(mdsr.getCreatedDate());
            record.setUpdatedDate(mdsr.getCreatedDate());
            record.setUpdatedBy(mdsr.getCreatedBy());
            record.setBenId(benId);
            record.setAshaId(userId);
            record.setAmount(Long.valueOf(immunizationActivity.getRate()));
            if (userRepo.getUserRole(userId).get(0).getStateId() == StateCode.AM.getStateCode()) {
                if (mdsr.getMdsrDeathCertFile() != null && mdsr.getMdsr1File() != null && mdsr.getMdsr2File() != null) {
                    record.setIsEligible(true);
                    recordRepo.save(record);

                } else {
                    record.setIsEligible(false);
                    IncentiveActivityRecord incentiveActivityRecord = recordRepo.save(record);
                    if (incentiveActivityRecord == null) {
                        recordRepo.save(record);
                        pendindDocService.updatePendingActivity(userId, mdsr.getId(), record.getId(), immunizationActivity.getId());

                    }

                }

            } else {
                recordRepo.save(record);

            }

        }
    }


}
