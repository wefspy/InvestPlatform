package com.example.investplatform.model.entity.emitent;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emitent_private_entrepreneurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitentPrivateEntrepreneur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emitent_id", nullable = false, unique = true)
    private Emitent emitent;

    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;

    @Column(name = "patronymic", length = 255)
    private String patronymic;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "birth_place", nullable = false, length = 500)
    private String birthPlace;

    @Column(name = "ogrnip", nullable = false, unique = true, length = 15)
    private String ogrnip;

    @Column(name = "inn", nullable = false, unique = true, length = 12)
    private String inn;

    @Column(name = "registration_address", nullable = false, columnDefinition = "text")
    private String registrationAddress;

    @Column(name = "snils", nullable = false, unique = true, length = 11)
    private String snils;

    @Column(name = "material_facts", columnDefinition = "text")
    private String materialFacts;

    @Column(name = "invested_current_year", nullable = false, precision = 18, scale = 2)
    private BigDecimal investedCurrentYear;

    @Version
    private Long version;
}
