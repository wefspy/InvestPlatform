package com.example.investplatform.model.entity.emitent;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "emitent_legal_entities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitentLegalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emitent_id", nullable = false, unique = true)
    private Emitent emitent;

    @Column(name = "full_name", nullable = false, length = 500)
    private String fullName;

    @Column(name = "short_name", length = 255)
    private String shortName;

    @Column(name = "ogrn", nullable = false, unique = true, length = 13)
    private String ogrn;

    @Column(name = "inn", nullable = false, unique = true, length = 10)
    private String inn;

    @Column(name = "kpp", length = 9)
    private String kpp;

    @Column(name = "legal_address", nullable = false, columnDefinition = "text")
    private String legalAddress;

    @Column(name = "postal_address", columnDefinition = "text")
    private String postalAddress;

    @Column(name = "okpo", length = 10)
    private String okpo;

    @Column(name = "okato", length = 11)
    private String okato;

    @Column(name = "organisation_form", length = 100)
    private String organisationForm;

    @Column(name = "material_facts", columnDefinition = "text")
    private String materialFacts;

    @Column(name = "invested_current_year", nullable = false, precision = 18, scale = 2)
    private BigDecimal investedCurrentYear;

    @Version
    private Long version;
}
