package com.iemr.flw.service.sync;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserSyncScopeTest {

    // Two mapping rows for service 1738 (villages merge), one for 1744, and a malformed entry.
    private final UserSyncScope scope = UserSyncScope.fromMappingRows(List.of(
            new Object[]{1738, "5443, 5444"},
            new Object[]{1738, "5445,x"},
            new Object[]{1744, "6677"},
            new Object[]{1750, null}));

    @Test
    void noVillagesAskedForMeansAllOfTheUsersVillagesForThatService() {
        assertEquals(List.of(5443, 5444, 5445), scope.resolve(1738, List.of()));
        assertEquals(List.of(6677), scope.resolve(1744, null));
    }

    @Test
    void askedForVillagesMustAllBeAssigned() {
        assertEquals(List.of(5443, 5445), scope.resolve(1738, List.of(5445, 5443, 5445)));
        assertThrows(IllegalArgumentException.class, () -> scope.resolve(1738, List.of(5443, 6677)));
    }

    @Test
    void unmappedServiceOrServiceWithoutVillagesIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> scope.resolve(9999, List.of()));
        assertThrows(IllegalArgumentException.class, () -> scope.resolve(1750, List.of()));
    }

    @Test
    void scopeHashIgnoresVillageOrderButNotContent() {
        assertEquals(UserSyncScope.scopeHash(1738, List.of(5443, 5444)), UserSyncScope.scopeHash(1738, List.of(5444, 5443)));
        assertNotEquals(UserSyncScope.scopeHash(1738, List.of(5443)), UserSyncScope.scopeHash(1738, List.of(5443, 5444)));
        assertNotEquals(UserSyncScope.scopeHash(1738, List.of(5443)), UserSyncScope.scopeHash(1744, List.of(5443)));
    }
}
