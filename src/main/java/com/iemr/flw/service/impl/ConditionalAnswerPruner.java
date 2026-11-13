/*
 * AMRIT – Accessible Medical Records via Integrated Technology
 * Integrated EHR (Electronic Health Records) Solution
 *
 * Copyright (C) "Piramal Swasthya Management and Research Institute"
 *
 * This file is part of AMRIT.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.iemr.flw.service.impl;

import com.iemr.flw.repo.iemr.OptionConditionRepo;
import com.iemr.flw.repo.iemr.QuestionResponseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Clears answers before a re-save so that SHOW_QUESTION children hidden by an edited parent
 * don't keep their stale answers. For every question being saved, its own answer and the answers
 * of all its descendants (children, grandchildren, …) are deleted; children that are still visible
 * are re-sent by the client alongside their parent and get re-inserted.
 *
 * @author Piramal Swasthya
 */
@RequiredArgsConstructor
@Component
public class ConditionalAnswerPruner {

    private final OptionConditionRepo optionConditionRepo;
    private final QuestionResponseRepo questionResponseRepo;

    /** parentQuestionId → questionIds shown by any of its options. Load once per save. */
    public Map<Long, Set<Long>> loadChildrenByParent(Long versionId) {
        Map<Long, Set<Long>> childrenByParent = new HashMap<>();
        for (Object[] edge : optionConditionRepo.findShowQuestionEdgesByVersionId(versionId)) {
            childrenByParent.computeIfAbsent((Long) edge[0], k -> new HashSet<>()).add((Long) edge[1]);
        }
        return childrenByParent;
    }

    /**
     * Deletes, within one section response, the answers of the given questions plus all their
     * descendants. Must run before any new answers are inserted, otherwise a parent processed after
     * its child would wipe the child's freshly inserted answer.
     */
    public void deleteWithDescendants(Long sectionResponseId, Collection<Long> questionIds,
            Map<Long, Set<Long>> childrenByParent) {
        if (questionIds.isEmpty()) {
            return;
        }
        Set<Long> toDelete = collectWithDescendants(questionIds, childrenByParent);
        questionResponseRepo.deleteBySectionResponseIdAndQuestionIdIn(sectionResponseId, toDelete);
    }

    /** BFS over the SHOW_QUESTION graph; the visited set also guards against misconfigured cycles. */
    static Set<Long> collectWithDescendants(Collection<Long> roots, Map<Long, Set<Long>> childrenByParent) {
        Set<Long> visited = new LinkedHashSet<>();
        Deque<Long> queue = new ArrayDeque<>(roots);
        while (!queue.isEmpty()) {
            Long questionId = queue.poll();
            if (visited.add(questionId)) {
                queue.addAll(childrenByParent.getOrDefault(questionId, Set.of()));
            }
        }
        return visited;
    }
}
