package com.example.investplatform.model.entity.emitent;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "emitent_le_minority_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitentLeMinorityInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emitent_legal_entity_id", nullable = false, unique = true)
    private EmitentLegalEntity emitentLegalEntity;

    @Column(name = "total_shares_count", nullable = false)
    private Integer totalSharesCount;

    @Column(name = "total_share_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal totalSharePercent;
}
