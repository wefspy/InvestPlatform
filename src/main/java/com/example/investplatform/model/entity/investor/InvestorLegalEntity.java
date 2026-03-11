package com.example.investplatform.model.entity.investor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "investor_legal_entities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestorLegalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id", nullable = false, unique = true)
    private Investor investor;

    @Column(name = "full_name", nullable = false, length = 500)
    private String fullName;

    @Column(name = "short_name", length = 255)
    private String shortName;

    @Column(name = "ogrn", nullable = false, unique = true, length = 13)
    private String ogrn;

    @Column(name = "inn", nullable = false, unique = true, length = 10)
    private String inn;

    @Column(name = "foreign_registration_info", columnDefinition = "text")
    private String foreignRegistrationInfo;

    @Column(name = "tin", length = 50)
    private String tin;

    @Column(name = "legal_address", nullable = false, columnDefinition = "text")
    private String legalAddress;

    @Column(name = "postal_address", columnDefinition = "text")
    private String postalAddress;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;
}
