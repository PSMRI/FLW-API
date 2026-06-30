# Stop TB — Future Roadmap

**Date:** 2026-06-30  
**Author:** vishwab1  
**Prerequisite:** Read `StopTB_Standard_Table_Integration.md` first for current implementation context.

---

## Why this document exists

The current dual-write implementation (FLW-API) writes Stop TB clinical data to both custom
`tb_stoptb_*` tables and standard AMRIT tables (`t_phy_vitals`, `t_Phy_GeneralExam` etc.).

This document captures what still needs to be done in future phases — HWC integration,
edit support, worklist, and BenFlowStatus advancement.

**None of these future changes require a mobile app change.**

---

## 1. HWC-API — Read common fields from standard tables

**Trigger:** When HWC doctor needs to see Stop TB beneficiary's vitals/examination history.

**Problem:** HWC-API currently queries standard tables with a `providerServiceMapID` filter:
```sql
SELECT * FROM t_phy_vitals
WHERE beneficiaryRegID = ? AND providerServiceMapID = ?  -- HWC's own PSMID
```
Stop TB data was written with Stop TB's PSMID → HWC query misses it.

**Fix — HWC-API only, remove PSMID filter:**
```sql
SELECT * FROM t_phy_vitals
WHERE beneficiaryRegID = ?  -- no PSMID filter, shows all service lines
ORDER BY CreatedDate DESC
```

**Affected HWC-API endpoints:**
- `getBenPhysicalVitalDetail`
- `getBenAnthropometryDetail`
- `getBenPhyGeneralExamination`
- `getBenChiefComplaint`
- `getBenPrescriptionDetail`

**Effort:** Small — query change only in HWC-API. No schema change. No FLW-API change.

---

## 2. HWC-API — Read TB specific data from custom tables directly

**Trigger:** When HWC doctor needs full TB clinical picture — screening questions, diagnostics,
Nikshay ID, treatment tracking.

Standard tables only hold common clinical fields. TB specific data lives in custom tables.
HWC-API needs to query these directly by `benRegID`.

**New queries needed in HWC-API:**

```java
// TB Screening
tbScreeningRepo.findByBenRegIDOrderByCreatedDateDesc(benRegID);

// Diagnostics (Nikshay ID, X-ray, Truenat, Sputum, Liquid Culture)
tbDiagnosticsRepo.findByBenRegIDOrderByCreatedDateDesc(benRegID);

// Suspected cases
tbSuspectedRepo.findByBenIdOrderByCreatedDateDesc(benId);

// Confirmed cases + treatment
tbConfirmedRepo.findByBenIdOrderByCreatedDateDesc(benId);
```

**Tables queried:**
| Table | Key data |
|---|---|
| `tb_screening` | Cough, fever, night sweats, weight loss, family history |
| `tb_stoptb_diagnostics` | Nikshay ID, X-ray result, Truenat result, Liquid Culture result |
| `tb_suspected` | Sputum result, chest X-ray, referral, TB/DRTB confirmed flag |
| `tb_confirmed_cases` | Regimen, treatment dates, adherence, outcome |

**Effort:** Small — new repo methods + new HWC-API service calls. No schema change.

---

## 3. FLW-API — Edit support for standard table dual-write

**Trigger:** When mobile app adds edit/update capability for nurse data.

**Current behaviour:** Dual-write only happens on first save (`isNew = true`). On re-save,
only `tb_stoptb_*` custom tables are updated. Standard tables keep the original values.

**What breaks:** If mobile edits vitals → `tb_stoptb_general_examination` gets new values
but `t_phy_vitals` keeps old values → HWC sees stale data.

### Changes in StopTBServiceImpl

```java
// Change from:
if (isNew) {
    generalExaminationRepo.updateVanSerialNo(exam.getId());
    dualWriteExamToStandardTables(exam, beneficiaryRegID, visit, createdBy, vanID, parkingPlaceID);
}

// Change to:
if (isNew) generalExaminationRepo.updateVanSerialNo(exam.getId());
dualWriteExamToStandardTables(exam, beneficiaryRegID, visit, createdBy, vanID, parkingPlaceID);
```

Same change for `saveGeneralOpd()`:
```java
// Remove isNew guard around dualWriteOpdToStandardTables
if (isNew) generalOpdRepo.updateVanSerialNo(opd.getId());
dualWriteOpdToStandardTables(opd, beneficiaryRegID, visit, createdBy, vanID, parkingPlaceID);
```

### Changes in dual-write helpers — upsert instead of insert

Each helper must find existing row first, update if found, insert if not:

```java
private void writeVitals(StopTBGeneralExamination exam, ...) {
    try {
        // Find existing row for this visit
        BenPhysicalVitalDetail existing =
                benPhysicalVitalRepo.findByBenVisitID(visit.getBenVisitId());
        BenPhysicalVitalDetail v = existing != null ? existing : new BenPhysicalVitalDetail();

        v.setBeneficiaryRegID(beneficiaryRegID);
        v.setBenVisitID(visit.getBenVisitId());
        v.setVisitCode(visit.getVisitCode());
        // ... set all fields
        benPhysicalVitalRepo.save(v);
    } catch (Exception e) {
        logger.error("writeVitals failed", e);
    }
}
```

Same upsert pattern for: `writeAnthropometry()`, `writePhyGeneralExam()`,
`writeChiefComplaint()`, `writePrescription()`.

### New repo methods needed (FLW-API)

```java
// BenPhysicalVitalRepo
BenPhysicalVitalDetail findByBenVisitID(Long benVisitID);

// BenAnthropometryRepo
BenAnthropometryDetail findByBenVisitID(Long benVisitID);

// PhyGeneralExaminationRepo
PhyGeneralExamination findByBenVisitID(Long benVisitID);

// BenChiefComplaintRepo
BenChiefComplaint findByBenVisitID(Long benVisitID);
```

> **Note:** `t_prescription` and `t_prescribeddrug` have a parent-child FK relationship.
> For prescription edit: find existing `PrescriptionDetail` by `benVisitID`, delete old
> `PrescribedDrugDetail` rows, re-insert fresh drug rows.

**Effort:** Small — remove isNew guard, add findByBenVisitID to 4 repos, convert helpers to upsert.

---

## 4. FLW-API — nurseFlag advancement

**Trigger:** When a web dashboard / supervisor screen is built for Stop TB.

**Current state:** `nurseFlag` in `i_ben_flow_outreach` is set to `1` at registration and
never advances because mobile never calls `flw-api/stoptb/nurse/submit`.

**What to implement:**

After each of the 3 mandatory nurse saves (generalExamination, tbScreening, generalOpd),
check if all 3 are complete for the same `visitCode`. If yes, auto-advance `nurseFlag = 9`.

```java
private void tryAdvanceNurseFlag(Long benRegID, Long visitCode, Integer psmID) {
    boolean examDone     = generalExaminationRepo
            .findByBeneficiaryRegIDAndVisitCode(benRegID, visitCode) != null;
    boolean screeningDone = tbScreeningRepo
            .findByBenRegIDAndVisitCode(benRegID, visitCode) != null;
    boolean opdDone      = generalOpdRepo
            .findByBenRegIDAndVisitCode(benRegID, visitCode) != null;

    if (examDone && screeningDone && opdDone) {
        benFlowStatusRepo.updateStopTBAfterNurseSubmit(benRegID, psmID);
        // updateStopTBAfterNurseSubmit() already exists in BenFlowStatusRepo
    }
}
```

Call `tryAdvanceNurseFlag()` at the end of each of the 3 save methods in `StopTBServiceImpl`.

**Effort:** Small — new private method in StopTBServiceImpl, reuses existing repo query.

---

## 5. HWC-API + HWC-UI — Stop TB referral worklist

**Trigger:** When HWC wants to track Stop TB beneficiaries referred to it.

**Current state:** When Stop TB nurse marks `referralToHWCNeeded = true`, FLW-API writes a
row to `t_benreferdetails` with `referredToInstituteName = 'HWC'` and
`referralReason = 'Stop TB Referral'`. HWC-API has no endpoint that surfaces these rows.

**HWC-API change needed:**

New query in `BenReferDetailsRepo`:
```java
@Query("SELECT r FROM BenReferDetails r WHERE r.beneficiaryRegID = :benRegID " +
       "AND r.referralReason = 'Stop TB Referral' AND r.deleted = false " +
       "ORDER BY r.createdDate DESC")
List<BenReferDetails> findStopTBReferrals(@Param("benRegID") Long benRegID);
```

Or for worklist (all referred beneficiaries for a provider):
```java
@Query("SELECT r FROM BenReferDetails r WHERE r.referredToInstituteName = 'HWC' " +
       "AND r.referralReason = 'Stop TB Referral' AND r.deleted = false")
List<BenReferDetails> findAllStopTBReferrals();
```

**HWC-UI change needed:**
- New worklist tab or filter: "Stop TB Referrals"
- Shows beneficiary name, referral date, FLW name

**Effort:** Medium — HWC-API new endpoint + HWC-UI new screen.

---

## Summary table

| # | Change | Service | Effort | Blocks on |
|---|---|---|---|---|
| 1 | Read standard tables without PSMID filter | HWC-API | Small | HWC team decision |
| 2 | Read TB specific custom tables | HWC-API | Small | HWC team decision |
| 3 | Edit support — upsert in dual-write helpers | FLW-API | Small | Mobile adding edit |
| 4 | nurseFlag auto-advancement | FLW-API | Small | Web dashboard being built |
| 5 | Stop TB referral worklist | HWC-API + HWC-UI | Medium | HWC team + product decision |

**No mobile change required for any of these.**  
**No schema change required for items 1, 2, 3, 4.**  
**Item 5 may need a column in `t_benreferdetails` for status tracking (pending/accepted/rejected) — TBD by HWC team.**
