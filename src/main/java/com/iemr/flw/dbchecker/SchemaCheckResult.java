package com.iemr.flw.dbchecker;

import java.time.LocalDateTime;
import java.util.*;

public class SchemaCheckResult {

    private final LocalDateTime checkedAt = LocalDateTime.now();

    // DB ke hisaab se grouped — key = "db_iemr" / "db_identity"
    private final Map<String, List<String>> missingTablesByDb  = new LinkedHashMap<>();
    private final Map<String, List<String>> missingColumnsByDb = new LinkedHashMap<>();
    private final Map<String, List<String>> typeMismatchesByDb = new LinkedHashMap<>();
    private final List<String> passedChecks = new ArrayList<>();

    // ── add helpers ──────────────────────────────────────────────────────────

    public void addMissingTable(String db, String table) {
        missingTablesByDb.computeIfAbsent(db, k -> new ArrayList<>()).add(table);
    }

    public void addMissingColumn(String db, String table, String col) {
        missingColumnsByDb.computeIfAbsent(db, k -> new ArrayList<>()).add(table + "." + col);
    }

    public void addTypeMismatch(String db, String table, String col, String expected, String actual) {
        typeMismatchesByDb.computeIfAbsent(db, k -> new ArrayList<>())
                .add(table + "." + col + " [expected=" + expected + ", actual=" + actual + "]");
    }

    public void addPassedCheck(String check) { passedChecks.add(check); }

    // ── query helpers ────────────────────────────────────────────────────────

    public boolean hasIssues() {
        return !missingTablesByDb.isEmpty() || !missingColumnsByDb.isEmpty() || !typeMismatchesByDb.isEmpty();
    }

    public Map<String, List<String>> getMissingTablesByDb()  { return missingTablesByDb; }
    public Map<String, List<String>> getMissingColumnsByDb() { return missingColumnsByDb; }
    public Map<String, List<String>> getTypeMismatchesByDb() { return typeMismatchesByDb; }
    public List<String>              getPassedChecks()       { return passedChecks; }
    public LocalDateTime             getCheckedAt()          { return checkedAt; }

    /** Flat list for backward compat / logging */
    public List<String> getMissingTables()  { return flatten(missingTablesByDb); }
    public List<String> getMissingColumns() { return flatten(missingColumnsByDb); }
    public List<String> getTypeMismatches() { return flatten(typeMismatchesByDb); }

    private List<String> flatten(Map<String, List<String>> map) {
        List<String> all = new ArrayList<>();
        map.values().forEach(all::addAll);
        return all;
    }

    public String getSummary() {
        if (!hasIssues()) return "✅ All schema checks passed";
        return String.format("❌ %d missing tables, %d missing columns, %d type mismatches",
                getMissingTables().size(), getMissingColumns().size(), getTypeMismatches().size());
    }
}