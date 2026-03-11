package com.example.investplatform.model.entity.security;

import com.example.investplatform.model.entity.emitent.Emitent;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "securities")
public class Security {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "securities_code", nullable = false, unique = true, length = 50)
    private String securitiesCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_classification_id", nullable = false)
    private SecurityClassification securityClassification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_category_id", nullable = false)
    private SecurityCategory securityCategory;

    @Column(name = "security_type", length = 100)
    private String securityType;

    @Column(name = "state_reg_num", nullable = false, length = 20)
    private String stateRegNum;

    @Column(name = "state_reg_date", nullable = false)
    private LocalDate stateRegDate;

    @Column(name = "isin", nullable = false, unique = true, length = 12)
    private String isin;

    @Column(name = "nominal_currency", nullable = false, length = 3)
    private String nominalCurrency;

    @Column(name = "nominal_value", nullable = false, precision = 18, scale = 4)
    private BigDecimal nominalValue;

    @Column(name = "quantity_in_issue", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantityInIssue;

    @Column(name = "quantity_placed", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantityPlaced;

    @Column(name = "form_issue", nullable = false, length = 20)
    private String formIssue;

    @Column(name = "cfi_code", length = 6)
    private String cfiCode;

    @Column(name = "financial_instrument_type", length = 10)
    private String financialInstrumentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emitent_id", nullable = false)
    private Emitent emitent;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;
}
