package com.example.investplatform.model.entity.emitent;

import com.example.investplatform.model.enums.PersonType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "emitent_le_shareholders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitentLeShareholder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emitent_legal_entity_id", nullable = false)
    private EmitentLegalEntity emitentLegalEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "person_type", nullable = false, length = 30)
    private PersonType personType;

    @Column(name = "full_name", nullable = false, length = 500)
    private String fullName;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "inn", nullable = false, length = 12)
    private String inn;

    @Column(name = "ogrn", length = 13)
    private String ogrn;

    @Column(name = "foreign_registration_info", columnDefinition = "text")
    private String foreignRegistrationInfo;

    @Column(name = "vote_share_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal voteSharePercent;

    @Column(name = "ownership_basis", nullable = false, length = 500)
    private String ownershipBasis;
}
