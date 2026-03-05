package com.iemr.flw.dbchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SchemaCheckStartupRunner {

    private static final Logger log = LoggerFactory.getLogger(SchemaCheckStartupRunner.class);

    @Autowired
    private SchemaCheckService schemaCheckService;

    @EventListener(ApplicationReadyEvent.class)
    public void checkOnStartup() {
        log.info("╔══════════════════════════════════════════════╗");
        log.info("║   🚀 FLW DB Schema Check — Deployment Start  ║");
        log.info("╚══════════════════════════════════════════════╝");

        SchemaCheckResult result = schemaCheckService.runFullSchemaCheck();

        if (result.hasIssues()) {
            log.warn("⚠️  SCHEMA ISSUES FOUND — DB aur code me mismatch hai!");
            log.warn("══════════════════════════════════════════════");

            // Missing Tables — DB ke hisaab se grouped
            if (!result.getMissingTablesByDb().isEmpty()) {
                log.error("📋 MISSING TABLES:");
                result.getMissingTablesByDb().forEach((db, tables) -> {
                    log.error("  [{}]", db);
                    tables.forEach(t -> log.error("    ❌ {}", t));
                });
            }

            // Missing Columns — DB ke hisaab se grouped
            if (!result.getMissingColumnsByDb().isEmpty()) {
                log.error("📋 MISSING COLUMNS:");
                result.getMissingColumnsByDb().forEach((db, cols) -> {
                    log.error("  [{}]", db);
                    cols.forEach(c -> log.error("    ❌ {}", c));
                });
            }

            // Type Mismatches — DB ke hisaab se grouped
            if (!result.getTypeMismatchesByDb().isEmpty()) {
                log.warn("📋 TYPE MISMATCHES:");
                result.getTypeMismatchesByDb().forEach((db, mismatches) -> {
                    log.warn("  [{}]", db);
                    mismatches.forEach(m -> log.warn("    ⚠️  {}", m));
                });
            }

            log.warn("══════════════════════════════════════════════");
            log.warn("Summary: {}", result.getSummary());
            log.warn("Full report: GET /api/schema/check");

        } else {
            log.info("✅ All {} tables OK — DB is in sync!", result.getPassedChecks().size());
        }

        log.info("══════════════════════════════════════════════");
    }
}