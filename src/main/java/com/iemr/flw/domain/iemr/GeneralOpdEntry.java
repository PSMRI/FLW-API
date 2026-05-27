package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "general_opd_entry") // Updated table name
public class GeneralOpdEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "asha_id")
    private String ashaId;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private String age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "registration_date")
    private String registrationDate;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "beneficiary_id")
    private String beneficiaryId;

    @Column(name = "visit_date")
    private String visitDate;

    @Column(name = "referred_to")
    private String referredTo;

    @Column(name = "follow_up_date")
    private String followUpDate;
}
