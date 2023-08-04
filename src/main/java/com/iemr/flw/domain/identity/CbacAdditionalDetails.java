package com.iemr.flw.domain.identity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "i_cbac_additional_details", schema = "db_identity")
@Data
public class CbacAdditionalDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cbacAdditionalDetailId;
    private Long cbacDetailsId;
    private String cbacCloudy;
    private Integer cbacCloudyPosi;
    private String cbacDiffreading;
    private Integer cbacDiffreadingPosi;
    private String cbacPainIneyes;
    private Integer cbacPainIneyesPosi;
    private String cbacRednessIneyes;
    private Integer cbacRednessIneyesPosi;
    private String cbacDiffInhearing;
    private Integer cbacDiffInhearingPosi;
    private String cbacFeelingUnsteady;
    private Integer cbacFeelingUnsteadyPosi;
    private String cbacSufferPhysicalDisability;
    private Integer cbacSufferPhysicalDisabilityPosi;
    private String cbacNeedingHelp;
    private Integer cbacNeedingHelpPosi;
    private String cbacForgettingNames;
    private Integer cbacForgettingNamesPosi;
    private String cbacTinglingPalm;
    private Integer cbacTinglingPalmPosi;
    private String cbacWhiteOrRedPatch;
    private Integer cbacWhiteOrRedPatchPosi;
}
