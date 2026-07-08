-- Adds the "Risk Factors" section (Key Population / Risk Factors + HIV Status)
-- to tb_screening, moved here from tb_stoptb_general_examination.

ALTER TABLE db_iemr.tb_screening
    ADD COLUMN key_population_risk_factor_ids TEXT NULL,
    ADD COLUMN key_population_risk_factors TEXT NULL,
    ADD COLUMN hiv_status_id INT NULL,
    ADD COLUMN hiv_status VARCHAR(20) NULL;

-- NOTE: columns key_population_risk_factor_ids, key_population_risk_factors,
-- hiv_status_id, hiv_status are left in place on db_iemr.tb_stoptb_general_examination
-- for historical data already captured there. Drop them separately once confirmed
-- they are no longer needed:
--
-- ALTER TABLE db_iemr.tb_stoptb_general_examination
--     DROP COLUMN key_population_risk_factor_ids,
--     DROP COLUMN key_population_risk_factors,
--     DROP COLUMN hiv_status_id,
--     DROP COLUMN hiv_status;
