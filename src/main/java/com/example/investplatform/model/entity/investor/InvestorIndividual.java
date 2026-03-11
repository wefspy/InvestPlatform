package com.example.investplatform.model.entity.investor;

import com.example.investplatform.model.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "investor_individuals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestorIndividual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id", nullable = false, unique = true)
    private Investor investor;

    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;

    @Column(name = "patronymic", length = 255)
    private String patronymic;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 15)
    private Gender gender;

    @Column(name = "citizenship", nullable = false, length = 100)
    private String citizenship;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "birth_place", nullable = false, length = 500)
    private String birthPlace;

    @Column(name = "id_doc_type", nullable = false, length = 50)
    private String idDocType;

    @Column(name = "id_doc_series", length = 20)
    private String idDocSeries;

    @Column(name = "id_doc_number", nullable = false, length = 50)
    private String idDocNumber;

    @Column(name = "id_doc_issued_date", nullable = false)
    private LocalDate idDocIssuedDate;

    @Column(name = "id_doc_issued_by", nullable = false, length = 500)
    private String idDocIssuedBy;

    @Column(name = "id_doc_department_code", length = 10)
    private String idDocDepartmentCode;

    @Column(name = "registration_address", nullable = false, columnDefinition = "text")
    private String registrationAddress;

    @Column(name = "residential_address", nullable = false, columnDefinition = "text")
    private String residentialAddress;

    @Column(name = "inn", length = 12)
    private String inn;

    @Column(name = "snils", length = 14)
    private String snils;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "invested_other_platforms", nullable = false, precision = 18, scale = 2)
    private BigDecimal investedOtherPlatforms;
}
