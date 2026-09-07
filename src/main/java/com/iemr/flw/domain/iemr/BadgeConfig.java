package com.iemr.flw.domain.iemr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Badge tuning as key/value rows (Badge LLD §5.2). Overrides the app's
 * compiled defaults without a release, e.g. "milestones.steady_syncer" -> "2,4,6,8".
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "badge_config", schema = "db_iemr")
public class BadgeConfig {

    @Id
    @Column(name = "config_key", length = 100)
    private String key;

    @Column(name = "config_value", length = 500, nullable = false)
    private String value;
}
