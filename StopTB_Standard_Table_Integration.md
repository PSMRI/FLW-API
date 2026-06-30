# Stop TB — Standard Table Integration

**Branch:** `vb/counselling`  
**Date:** 2026-06-30  
**Author:** vishwab1

---

## 1. Why this was done

Stop TB's clinical data was siloed entirely inside custom `tb_stoptb_*` tables. No other AMRIT
service (HWC, MMU) could see a Stop TB beneficiary's record — referrals, vitals, prescriptions
were invisible across service lines.

This integration dual-writes Stop TB clinical data into the same standard tables that MMU/HWC
already use, making beneficiary records portable across services without any change to the
mobile app.

---

## 2. What was implemented

### 2.1 Visit layer — `t_benvisitdetail`

**Problem:** Stop TB used its own `tb_stoptb_visit` mirror table. `benVisitID` was null in all
clinical tables. HWC/MMU had no visit anchor to join on.

**Fix:** `TBStopVisitServiceImpl` was completely rewritten to create rows in `t_benvisitdetail`
instead of `tb_stoptb_visit`. The old entity and repo were deleted.

**Key behaviour (get-or-create-for-today):**
- First save of the day → creates a new `t_benvisitdetail` row
- Subsequent saves same day → finds and reuses the same row
- Next camp day → creates a new row (separate visit, history preserved)

**VisitCode formula (same as MMU):**
```
visitCode = "1" + vanID(zero-padded 5 digits) + benVisitId(zero-padded 8 digits)
Example:  vanID=1, benVisitId=42  →  10000100000042
```

**Files changed:**
- `TBStopVisitService.java` — interface return type changed to `BenVisitDetail`
- `TBStopVisitServiceImpl.java` — full rewrite, uses `BenVisitDetailsRepo`
- `BenVisitDetailsRepo.java` — added `findStopTBVisitForToday()` and `countStopTBVisits()`
- `TBStopVisit.java` — **DELETED** (orphaned)
- `TBStopVisitRepo.java` — **DELETED** (orphaned)

---

### 2.2 Standard table dual-writes

All dual-writes happen inside `StopTBServiceImpl`. Custom `tb_stoptb_*` tables continue to
be written unchanged. Standard table writes are **additive and insert-only** (no edit
capability exists in the mobile).

Each write is wrapped in its own try/catch — a standard-table failure never breaks the
primary Stop TB save.

#### `saveGeneralExamination()` writes to:

| Standard Table | Data source | Fields written |
|---|---|---|
| `t_phy_anthropometry` | `i_beneficiarydetails.otherFields` (registration) | `Height_cm`, `Weight_Kg`, `BMI` |
| `t_phy_vitals` | otherFields + request | `Temperature` (from otherFields), `PulseRate`, `SystolicBP_1stReading`, `DiastolicBP_1stReading`, `BloodGlucose_Random` |
| `t_Phy_GeneralExam` | request | `Pallor`, `Jaundice` (←icterus), `Cyanosis`, `Clubbing`, `Lymphadenopathy`, `Edema` (←oedema) |
| `t_benreferdetails` | request | written only when `referralToHWCNeeded = true` |

> **Note:** Height/weight/bmi/temperature come from registration `otherFields` because the
> mobile's `GeneralExaminationSaveRequest` does not include these fields. The mobile hardcodes
> them null in `VitalScreenFragment.saveVitals()`. They are captured at registration and synced
> to Identity-API via TM-API.

#### `saveGeneralOpd()` writes to:

| Standard Table | Fields written |
|---|---|
| `t_benchiefcomplaint` | `ChiefComplaint` (free text from request) |
| `t_prescription` | `Instruction` (from `notes` field) |
| `t_prescribeddrug` | `GenericDrugName` (medication), `Dose`, `Frequency`, `Duration`, linked to `PrescriptionID` |

All 7 standard tables share the same `benVisitID` and `VisitCode` as the join key.

---

### 2.3 New entities and repos created

All in package `com.iemr.flw.domain.iemr` / `com.iemr.flw.repo.iemr`:

| Entity | Table | Repo |
|---|---|---|
| `BenAnthropometryDetail` | `t_phy_anthropometry` | `BenAnthropometryRepo` |
| `BenPhysicalVitalDetail` | `t_phy_vitals` | `BenPhysicalVitalRepo` |
| `PhyGeneralExamination` | `t_Phy_GeneralExam` | `PhyGeneralExaminationRepo` |
| `BenChiefComplaint` | `t_benchiefcomplaint` | `BenChiefComplaintRepo` |
| `PrescriptionDetail` | `t_prescription` | `PrescriptionDetailRepo` |
| `PrescribedDrugDetail` | `t_prescribeddrug` | `PrescribedDrugDetailRepo` |

All 6 standard tables already existed in `db_iemr`. No CREATE TABLE needed for these.
`BenReferDetails` and `BenReferDetailsRepo` already existed in FLW-API — just wired up.

---

### 2.4 Service files updated

All Stop TB service files that previously used `TBStopVisit` and `visit.getId()` were updated
to use `BenVisitDetail` and `visit.getVisitCode()`:

| File | Change |
|---|---|
| `StopTBServiceImpl` | `saveGeneralExamination`, `saveNurseTBScreening`, `saveGeneralOpd` — all updated |
| `TBSuspectedServiceImpl` | `save()` — updated |
| `TBConfirmedCaseServiceImpl` | `save()` — updated |

The `visitCode` column on `tb_stoptb_suspected` and `tb_stoptb_confirmed_cases` is still used
for per-visit keying. The value written is now the 14-digit standard visitCode instead of the
old `tb_stoptb_visit` PK.

---

## 3. DB migration required

Run these two statements on the server. The standard tables (`t_phy_anthropometry` etc.)
already exist in `db_iemr` — no CREATE needed for them.

```sql
-- Required: visitCode column added to these two tables when per-visit history was introduced
ALTER TABLE tb_stoptb_suspected       ADD COLUMN visitCode BIGINT;
ALTER TABLE tb_stoptb_confirmed_cases ADD COLUMN visitCode BIGINT;

-- DO NOT run: tb_stoptb_visit is replaced by t_benvisitdetail
-- CREATE TABLE tb_stoptb_visit  ← SKIP THIS
```

---

## 4. BenFlowStatus (`i_ben_flow_outreach`) — current state

| Flag | Set when | Value |
|---|---|---|
| `nurseFlag` | Registration | `1` (pending for nurse) |
| `nurseFlag` | `submitNurseData()` | `9` (nurse done) |
| `doctorFlag` | `routeToStopTBCounsellor()` | `1` (pending for counsellor) |

**Known gap:** Mobile does not call `flw-api/stoptb/nurse/submit` (app is live, cannot change).
So `nurseFlag` stays at `1` forever after registration.

**Why this is not breaking:** The mobile also does not call the nurse worklist or registrar
worklist endpoints — both are commented out in `AmritApiService.kt`. The mobile uses
`flw-api/beneficiary/getBeneficiaryData` instead and manages its own local state.

**Counsellor:** Finds confirmed TB cases via direct `tb_confirmed_cases` query, not via
`doctorFlag`. This works correctly today.

**Future action (when web dashboard is built):** Wire up `nurseFlag` advancement by checking
all 3 mandatory nurse saves (generalExamination + tbScreening + generalOpd) exist for the
same `visitCode`, then auto-advance `nurseFlag = 9`. The query
`updateStopTBAfterNurseSubmit()` in `BenFlowStatusRepo` already exists for this.

---

## 5. Complete mobile API list (live, as of 2026-06-30)

### Auth
| Endpoint |
|---|
| `POST common-api/user/userAuthenticate` |
| `POST common-api/user/refreshToken` |
| `GET  flw-api/user/getUserDetail` |
| `POST common-api/firebaseNotification/userToken` |

### Beneficiary Registration
| Endpoint |
|---|
| `POST tm-api/registrar/registrarBeneficaryRegistrationNew` |
| `POST identity-api/rmnch/syncDataToAmrit` |
| `POST flw-api/beneficiary/getBeneficiaryData` |

### Consent / OTP
| Endpoint |
|---|
| `POST common-api/beneficiaryConsent/sendConsent` |
| `POST common-api/beneficiaryConsent/resendConsent` |
| `POST common-api/beneficiaryConsent/validateConsent` |

### Stop TB — Nurse
| Endpoint |
|---|
| `POST flw-api/stoptb/nurse/generalExamination/save` |
| `POST flw-api/stoptb/nurse/generalExamination/getAll` |
| `POST flw-api/stoptb/nurse/tbScreening/save` |
| `POST flw-api/stoptb/nurse/tbScreening/getAll` |
| `POST flw-api/stoptb/nurse/generalOpd/save` |
| `POST flw-api/stoptb/nurse/generalOpd/getAll` |
| `POST flw-api/stoptb/nurse/diagnostics/save` |
| `POST flw-api/stoptb/nurse/diagnostics/getAll` |

### Stop TB — Suspected / Confirmed
| Endpoint |
|---|
| `POST flw-api/tb/suspected/saveAll` |
| `POST flw-api/tb/suspected/getAll` |
| `POST flw-api/tb/confirmed/save` |
| `GET  flw-api/tb/confirmed/getAll` |

### Counselling (Dynamic Form)
| Endpoint |
|---|
| `GET  flw-api/dynamicForm/getAllForms` |
| `GET  common-api/dynamicForm/form/{formId}/fields` |
| `POST flw-api/counselling/save` |
| `POST flw-api/dynamicForm/response/submitBulk` |
| `POST flw-api/dynamicForm/response/complete` |
| `GET  flw-api/dynamicForm/response/getByBeneficiary` |
| `GET  flw-api/dynamicForm/response/getCompletedBeneficiaries` |

### CBAC / NCD
| Endpoint |
|---|
| `POST hwc-api/NCD/getByUserCbacDetails` |
| `POST hwc-api/NCD/save/nurseData` |
| `POST hwc-api/NCD/save/referDetails` |

### ABHA / Health ID
| Endpoint |
|---|
| `POST fhir-api/healthIDWithUID/createHealthIDWithUID` |
| `POST fhir-api/healthID/getBenhealthID` |
| `POST fhir-api/healthIDRecord/mapHealthIDToBeneficiary` |
| `POST fhir-api/healthIDRecord/addHealthIdRecord` |
| `POST fhir-api/healthIDCard/generateOTP` |
| `POST fhir-api/healthIDCard/verifyOTPAndGenerateHealthCard` |

### Other Disease Screens
| Endpoint |
|---|
| `POST flw-api/disease/kalaAzar/saveAll` |
| `POST flw-api/disease/malaria/saveAll` |
| `POST flw-api/disease/leprosy/saveAll` |
| `POST flw-api/disease/leprosy/followUp/saveAll` |
| `POST flw-api/disease/leprosy/followUp/getAll` |
| `POST flw-api/disease/aesJe/saveAll` |
| `POST flw-api/disease/filaria/saveAll` |
| `POST flw-api/disease/getAllDisease` |
| `POST flw-api/follow-up/save` |
| `POST flw-api/follow-up/get` |
| `POST flw-api/child-care/hbncVisit/saveAll` |
| `POST flw-api/disease/cdtfVisit/saveAll` |
| `POST flw-api/disease/cdtfVisit/getAll` |

### Commented out / never called (documented for future reference)
| Endpoint | Reason |
|---|---|
| `POST flw-api/stoptb/nurse/worklist` | Mobile uses getBeneficiaryData instead |
| `POST flw-api/stoptb/registrar/worklist` | Mobile uses getBeneficiaryData instead |
| `POST flw-api/stoptb/nurse/submit` | Mobile is live — cannot add new call; nurseFlag stays 1 (acceptable, see Section 4) |
| `POST hwc-api/common/getBenReferDetailsByCreatedBy` | Referral pull not needed in Stop TB |
| `POST hwc-api/sync/generalOPDNurseFormDataToServer` | Abandoned earlier attempt at standard-table integration |
| `POST identity-api/id/getByBenId` | getBenHealthID used instead |

---

## 6. What HWC can now see

Once the DB migration SQL is run and FLW-API is deployed, any service reading from `db_iemr`
standard tables will find Stop TB beneficiary data joined by `benVisitID` / `VisitCode`:

| Table | Content |
|---|---|
| `t_benvisitdetail` | One row per camp day, `visitCategory = 'Stop TB'` |
| `t_phy_anthropometry` | Height, weight, BMI (from registration) |
| `t_phy_vitals` | Temperature (from registration), pulse, BP, RBS (from nurse exam) |
| `t_Phy_GeneralExam` | Pallor, jaundice, cyanosis, clubbing, lymphadenopathy, edema |
| `t_benchiefcomplaint` | Chief complaint text |
| `t_prescription` | Notes/instructions |
| `t_prescribeddrug` | Medication, dose, frequency, duration |
| `t_benreferdetails` | HWC referral (only when `referralToHWCNeeded = true`) |

> **Note:** HWC's referral-receiving worklist endpoints (`getBenPreviousReferralHistoryDetails`,
> `getBenReferDetailsByCreatedBy`) have zero callers on HWC-UI and HWC-Mobile-App as of today.
> Writing `t_benreferdetails` is correct for audit/reporting. Surfacing it as a live HWC
> worklist is a separate, future piece of work.

---

## 7. What is NOT done (future scope)

| Item | Detail |
|---|---|
| `nurseFlag` auto-advancement | Needs web dashboard to be useful. Logic: check all 3 saves (exam + screening + OPD) exist for same `visitCode` → set `nurseFlag=9`. Query already exists in `BenFlowStatusRepo.updateStopTBAfterNurseSubmit()` |
| HWC worklist for Stop TB referrals | HWC-API and HWC-UI changes needed to surface `t_benreferdetails` rows |
| Duplicate beneficiary detection | Requires new mobile screen — no server-side gap |
| Beneficiary edit/update | Requires new mobile screen — no edit endpoint exists anywhere in Stop TB |
| `height`/`weight`/`bmi`/`respiratoryRate`/`spo2` in nurse exam | Mobile captures these locally (`VitalCache`) but `GeneralExaminationSaveRequest.from()` hardcodes them null. Would require mobile change to send live |
