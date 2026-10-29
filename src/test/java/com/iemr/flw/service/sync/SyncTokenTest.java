package com.iemr.flw.service.sync;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SyncTokenTest {

    private static final String UUID = "3f2a6c1e-1b2c-11ef-9f1a-0242ac120002";

    @Test
    void deltaRoundTrips() {
        SyncToken t = SyncToken.parse(SyncToken.delta(UUID, 1000, 1761700000L).encode());
        assertEquals(SyncToken.Kind.DELTA, t.kind());
        assertEquals(UUID, t.serverId());
        assertEquals(1000, t.seq());
        assertEquals(1761700000L, t.issuedAt());
    }

    @Test
    void snapshotRoundTrips() {
        SyncToken t = SyncToken.parse(SyncToken.snapshot(UUID, 5230, 3, 900).encode());
        assertEquals(SyncToken.Kind.SNAPSHOT, t.kind());
        assertEquals(5230, t.seq());
        assertEquals(3, t.typeIndex());
        assertEquals(900, t.afterKey());
    }

    @Test
    void unreadableTokensMeanStartOver() {
        assertNull(SyncToken.parse(null));
        assertNull(SyncToken.parse(""));
        assertNull(SyncToken.parse("garbage"));
        assertNull(SyncToken.parse("d." + UUID + ".x.1"));
        assertNull(SyncToken.parse("d..1.1"));
        assertNull(SyncToken.parse("q." + UUID + ".1.1"));
    }

    @Test
    void everyLogEntityMapsToOneType() {
        for (StopTBSyncType t : StopTBSyncType.values()) {
            assertSame(t, StopTBSyncType.fromLogEntity(t.logEntity()));
        }
        assertNull(StopTBSyncType.fromLogEntity("unknown"));
    }
}
