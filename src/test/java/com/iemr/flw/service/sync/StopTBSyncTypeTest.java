package com.iemr.flw.service.sync;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StopTBSyncTypeTest {

    @Test
    void childKeyQueriesTargetTheGivenBeneficiariesWithoutPaging() {
        for (StopTBSyncType t : StopTBSyncType.values()) {
            if (t == StopTBSyncType.BENEFICIARY) continue;
            String sql = t.keysForBeneficiariesSql();
            assertTrue(sql.contains(":regIds"), t + ": " + sql);
            assertFalse(sql.contains(":after"), t + ": " + sql);
            assertFalse(sql.contains(":limit"), t + ": " + sql);
            assertFalse(sql.contains(":villages"), t + ": " + sql);
            System.out.println(t + " | " + sql);
        }
    }
}
