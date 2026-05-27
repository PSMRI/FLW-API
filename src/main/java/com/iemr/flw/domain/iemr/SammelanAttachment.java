package com.iemr.flw.domain.iemr;

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
}