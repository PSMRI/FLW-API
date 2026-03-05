package com.iemr.flw.dbchecker;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * ExpectedSchema.java likhne ki zaroorat NAHI!
 *
 * Ye service JPA EntityManagerFactory se seedha entity metadata padhta hai —
 * @Table, @Column, @Entity sab automatically scan hota hai.
 *
 * Primary EMF   -> domain/iemr/   entities -> db_iemr  DataSource se compare
 * Secondary EMF -> domain/identity/ entities -> db_identity DataSource se compare
 */
@Service
public class SchemaCheckService {

    private static final Logger log = LoggerFactory.getLogger(SchemaCheckService.class);

    // Primary DB (db_iemr)
    @Autowired
    @Qualifier("entityManagerFactory")
    private EntityManagerFactory iemrEmf;

    @Autowired
    @Qualifier("dataSource")
    private DataSource iemrDataSource;

    // Secondary DB (db_identity)
    @Autowired
    @Qualifier("secondaryEntityManagerFactory")
    private EntityManagerFactory identityEmf;

    @Autowired
    @Qualifier("secondaryDataSource")
    private DataSource identityDataSource;

    // ─────────────────────────────────────────────────────────────────────────
    //  Public entry point
    // ─────────────────────────────────────────────────────────────────────────

    public SchemaCheckResult runFullSchemaCheck() {
        SchemaCheckResult result = new SchemaCheckResult();

        log.info("Scanning JPA entities for db_iemr ...");
        checkEntities(iemrEmf, iemrDataSource, "db_iemr", result);

        log.info("Scanning JPA entities for db_identity ...");
        checkEntities(identityEmf, identityDataSource, "db_identity", result);

        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Core: scan each @Entity, compare with live DB
    // ─────────────────────────────────────────────────────────────────────────

    private void checkEntities(EntityManagerFactory emf, DataSource ds,
                               String dbLabel, SchemaCheckResult result) {
        try (Connection conn = ds.getConnection()) {
            DatabaseMetaData meta  = conn.getMetaData();
            String catalog = conn.getCatalog();
            String schema  = conn.getSchema();

            Set<String> existingTables = getExistingTables(meta, catalog, schema);

            // JPA metamodel se saari entity types lo — koi hardcode nahi
            Set<EntityType<?>> entities = emf.getMetamodel().getEntities();
            log.info("[{}] Found {} JPA entities to check", dbLabel, entities.size());

            for (EntityType<?> entity : entities) {
                Class<?> javaClass = entity.getJavaType();

                // @Table annotation se actual DB table name lo
                String tableName = getTableName(javaClass);

                boolean tableExists = existingTables.stream()
                        .anyMatch(t -> t.equalsIgnoreCase(tableName));

                if (!tableExists) {
                    result.addMissingTable(dbLabel, tableName);
                    log.warn("  ❌ Missing table: {}.{}", dbLabel, tableName);
                    continue;
                }

                // Live DB ke columns fetch karo
                Map<String, String> actualCols = getActualColumns(meta, catalog, schema, tableName);

                // Entity ke har attribute ka column check karo
                checkEntityColumns(entity, javaClass, dbLabel, tableName, actualCols, result);

                result.addPassedCheck(dbLabel + "." + tableName);
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB connection failed [" + dbLabel + "]: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Column-level check
    // ─────────────────────────────────────────────────────────────────────────

    private void checkEntityColumns(EntityType<?> entity, Class<?> javaClass,
                                    String dbLabel, String tableName,
                                    Map<String, String> actualCols,
                                    SchemaCheckResult result) {

        for (Attribute<?, ?> attr : entity.getAttributes()) {
            // Relationships (@OneToMany etc.) skip karo — unke column table me nahi hote
            if (attr.isAssociation()) continue;
            if (attr.isCollection()) continue;

            String colName  = getColumnName(javaClass, attr.getName());
            String javaType = attr.getJavaType().getSimpleName();
            String expectDb = javaTypeToDbType(javaType);

            String key = colName.toLowerCase();
            if (!actualCols.containsKey(key)) {
                result.addMissingColumn(dbLabel, tableName, colName);
            } else {
                String actual = actualCols.get(key);
                if (!isCompatible(expectDb, actual)) {
                    result.addTypeMismatch(dbLabel, tableName, colName, expectDb, actual);
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Reflection helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @Table(name="...") se table name lo, fallback: class name lowercase
     */
    private String getTableName(Class<?> clazz) {
        jakarta.persistence.Table tableAnn = clazz.getAnnotation(jakarta.persistence.Table.class);
        if (tableAnn != null && !tableAnn.name().isBlank()) {
            return tableAnn.name();
        }
        // Fallback: entity name lowercase
        jakarta.persistence.Entity entityAnn = clazz.getAnnotation(jakarta.persistence.Entity.class);
        return clazz.getSimpleName().toLowerCase();
    }

    /**
     * @Column(name="...") se column name lo — field + superclass fields dono check karta hai
     */
    private String getColumnName(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(fieldName);
                jakarta.persistence.Column col = field.getAnnotation(jakarta.persistence.Column.class);
                if (col != null && !col.name().isBlank()) {
                    return col.name();
                }
                // @Id bhi check karo (sometimes @Column missing hota hai ID pe)
                jakarta.persistence.JoinColumn joinCol = field.getAnnotation(jakarta.persistence.JoinColumn.class);
                if (joinCol != null && !joinCol.name().isBlank()) {
                    return joinCol.name();
                }
                return fieldName; // fallback: same as Java field name
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return fieldName;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DB metadata helpers
    // ─────────────────────────────────────────────────────────────────────────

    private Set<String> getExistingTables(DatabaseMetaData meta, String catalog, String schema)
            throws SQLException {
        Set<String> tables = new HashSet<>();
        try (ResultSet rs = meta.getTables(catalog, schema, "%", new String[]{"TABLE", "VIEW"})) {
            while (rs.next()) tables.add(rs.getString("TABLE_NAME"));
        }
        return tables;
    }

    private Map<String, String> getActualColumns(DatabaseMetaData meta, String catalog,
                                                 String schema, String tableName) throws SQLException {
        Map<String, String> cols = new HashMap<>();
        for (String name : new String[]{tableName, tableName.toUpperCase(), tableName.toLowerCase()}) {
            try (ResultSet rs = meta.getColumns(catalog, schema, name, "%")) {
                while (rs.next())
                    cols.put(rs.getString("COLUMN_NAME").toLowerCase(),
                            rs.getString("TYPE_NAME").toUpperCase());
            }
            if (!cols.isEmpty()) break;
        }
        return cols;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Type mapping
    // ─────────────────────────────────────────────────────────────────────────

    private String javaTypeToDbType(String javaType) {
        return switch (javaType) {
            case "Long", "long", "BigInteger"         -> "BIGINT";
            case "Integer", "int"                     -> "INT";
            case "Short", "short"                     -> "SMALLINT";
            case "String"                             -> "VARCHAR";
            case "Double", "double"                   -> "DOUBLE";
            case "Float", "float"                     -> "FLOAT";
            case "BigDecimal"                         -> "DECIMAL";
            case "Boolean", "boolean"                 -> "BOOLEAN";
            case "Timestamp", "LocalDateTime"         -> "TIMESTAMP";
            case "LocalDate", "Date"                  -> "DATE";
            case "byte[]"                             -> "BLOB";
            default                                   -> "VARCHAR";
        };
    }

    private boolean isCompatible(String expected, String actual) {
        String e = expected.toUpperCase(), a = actual.toUpperCase();
        if (e.equals(a)) return true;
        Map<String, List<String>> aliases = Map.of(
                "BIGINT",    List.of("BIGINT","INT8","BIGSERIAL","LONG"),
                "INT",       List.of("INT","INT4","INTEGER","SERIAL","MEDIUMINT"),
                "SMALLINT",  List.of("SMALLINT","INT2","TINYINT"),
                "VARCHAR",   List.of("VARCHAR","CHARACTER VARYING","NVARCHAR","TEXT","CHAR",
                        "LONGTEXT","MEDIUMTEXT","TINYTEXT","JSON"),
                "DOUBLE",    List.of("DOUBLE","FLOAT8","DECIMAL","NUMERIC","FLOAT"),
                "FLOAT",     List.of("FLOAT","FLOAT4","REAL"),
                "BOOLEAN",   List.of("BOOLEAN","BOOL","BIT","TINYINT"),
                "TIMESTAMP", List.of("TIMESTAMP","DATETIME","TIMESTAMP WITHOUT TIME ZONE"),
                "DATE",      List.of("DATE","DATETIME"),
                "DECIMAL",   List.of("DECIMAL","NUMERIC","DOUBLE","FLOAT8")
        );
        return aliases.getOrDefault(e, List.of(e)).stream().anyMatch(a::contains);
    }
}