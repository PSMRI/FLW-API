package com.iemr.flw.domain.iemr;

import java.sql.Timestamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sammelan_attachment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SammelanAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sammelan_id", nullable = false)
    private SammelanRecord sammelanRecord;


    private String fileName;
    private String fileType;
    @Lob
    private byte[] fileData;

    @Column(name = "synced_by")
    private String syncedBy;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "synced_date")
    private Timestamp syncedDate;
}