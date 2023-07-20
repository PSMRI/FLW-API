package com.iemr.flw.domain;

import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "t_delivery_outcome", schema = "db_iemr")
public class DeliveryOutcome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "delivery_date")
    private Timestamp dateOfDelivery;

    @Column(name = "delivery_time")
    private Time timeOfDelivery;

    @Column(name = "delivery_place")
    private String placeOfDelivery;

    @Column(name = "delivery_type")
    private String typeOfDelivery;

    @Column(name = "had_complications")
    private Boolean hadComplications;

    @Column(name = "complication")
    private String complication;

    @Column(name = "death_cause")
    private String causeOfDeath;

    @Column(name = "other_death_cause")
    private String otherCauseOfDeath;

    @Column(name = "other_complication")
    private String otherComplication;

    @Column(name = "delivery_outcome")
    private Integer deliveryOutcome;

    @Column(name = "live_birth")
    private Integer liveBirth;

    @Column(name = "still_birth")
    private Integer stillBirth;

    @Column(name = "discharge_date")
    private Timestamp dateOfDischarge;

    @Column(name = "discharge_time")
    private Time timeOfDischarge;

    @Column(name = "is_jsybeneficiary")
    private Boolean isJSYBenificiary;
}
