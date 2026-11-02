package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.BadgeEarned;
import com.iemr.flw.dto.iemr.BadgeEarnedDTO;
import com.iemr.flw.repo.iemr.BadgeConfigRepo;
import com.iemr.flw.repo.iemr.BadgeEarnedRepo;
import com.iemr.flw.repo.iemr.BadgeStreakFreezeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        when(earnedRepo.existsByUserIdAndBadgeIdAndLevelAndAwardKey(960, "steady_syncer", 1, ""))
                .thenReturn(true);
        when(earnedRepo.existsByUserIdAndBadgeIdAndLevelAndAwardKey(960, "steady_syncer", 2, ""))
                .thenReturn(false);

        int inserted = service.saveEarned(960, List.of(
                new BadgeEarnedDTO("steady_syncer", 1, 10L, ""),
                new BadgeEarnedDTO("steady_syncer", 2, 20L, "")));

        assertEquals(1, inserted);
        verify(earnedRepo, times(1)).save(any(BadgeEarned.class));
    }

    /**
     * The whole point of the award key: a quarterly badge earned in two quarters is two
     * awards at the same level, and without the key the second one is silently dropped.
     */
    @Test
    void saveEarned_keepsTwoAwardsOfTheSameLevelWithDifferentKeys() {
        when(earnedRepo.existsByUserIdAndBadgeIdAndLevelAndAwardKey(
                eq(960), eq("complete_worker"), eq(1), anyString())).thenReturn(false);

        int inserted = service.saveEarned(960, List.of(
                new BadgeEarnedDTO("complete_worker", 1, 10L, "2026-Q2"),
                new BadgeEarnedDTO("complete_worker", 1, 20L, "2026-Q3")));

        assertEquals(2, inserted);
        verify(earnedRepo, times(2)).save(any(BadgeEarned.class));
    }

    /** An older client sends no key at all; it must behave as it did before the field existed. */
    @Test
    void saveEarned_treatsAMissingAwardKeyAsEmpty() {
        when(earnedRepo.existsByUserIdAndBadgeIdAndLevelAndAwardKey(960, "steady_syncer", 1, ""))
                .thenReturn(false);

        assertEquals(1, service.saveEarned(960, List.of(
                new BadgeEarnedDTO("steady_syncer", 1, 10L, null))));

        ArgumentCaptor<BadgeEarned> saved = ArgumentCaptor.forClass(BadgeEarned.class);
        verify(earnedRepo).save(saved.capture());
        assertEquals("", saved.getValue().getAwardKey());
    }

    @Test
    void saveEarned_ignoresMalformedEntriesAndEmptyLists() {
        assertEquals(0, service.saveEarned(960, null));
        assertEquals(0, service.saveEarned(960, List.of(new BadgeEarnedDTO(null, null, null, ""))));
        verify(earnedRepo, never()).save(any());
    }
}
