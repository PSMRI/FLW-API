package com.iemr.flw.domain.iemr;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incentive_pending_activity", schema = "db_iemr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncentivePendingActivity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "activity_id", nullable = false)
    private Long activityId;
    
    @Column(name = "record_id", nullable = false)
    private Long recordId;
    
    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(name = "created_date")
    private Date createdDate;
    
    @Column(name = "updated_date")
    private Date updatedDate;
}
