package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.BadgeEarned;
import com.iemr.flw.dto.iemr.BadgeEarnedDTO;
import com.iemr.flw.repo.iemr.BadgeConfigRepo;
import com.iemr.flw.repo.iemr.BadgeEarnedRepo;
import com.iemr.flw.repo.iemr.BadgeStreakFreezeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BadgeServiceTest {

    @Mock private BadgeConfigRepo configRepo;
    @Mock private BadgeStreakFreezeRepo freezeRepo;
    @Mock private BadgeEarnedRepo earnedRepo;

    private BadgeService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new BadgeService(configRepo, freezeRepo, earnedRepo);
    }

    @Test
    void saveEarned_skipsRowsAlreadyKnown_idempotentReupload() {
        when(earnedRepo.existsByUserIdAndBadgeIdAndLevel(960, "steady_syncer", 1)).thenReturn(true);
        when(earnedRepo.existsByUserIdAndBadgeIdAndLevel(960, "steady_syncer", 2)).thenReturn(false);

        int inserted = service.saveEarned(960, List.of(
                new BadgeEarnedDTO("steady_syncer", 1, 10L),
                new BadgeEarnedDTO("steady_syncer", 2, 20L)));

        assertEquals(1, inserted);
        verify(earnedRepo, times(1)).save(any(BadgeEarned.class));
    }

    @Test
    void saveEarned_ignoresMalformedEntriesAndEmptyLists() {
        assertEquals(0, service.saveEarned(960, null));
        assertEquals(0, service.saveEarned(960, List.of(new BadgeEarnedDTO(null, null, null))));
        verify(earnedRepo, never()).save(any());
    }
}
