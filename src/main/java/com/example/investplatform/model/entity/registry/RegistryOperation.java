package com.example.investplatform.model.entity.registry;

import com.example.investplatform.model.entity.account.PersonalAccount;
import com.example.investplatform.model.entity.security.Security;
import com.example.investplatform.model.enums.OperationKind;
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
@Table(name = "registry_operations")
public class RegistryOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_type_id", nullable = false)
    private RegistryOperationType operationType;

    @Column(name = "operation_name", nullable = false, length = 500)
    private String operationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_kind", length = 30)
    private OperationKind operationKind;

    @Column(name = "processing_datetime", nullable = false)
    private LocalDateTime processingDatetime;

    @Column(name = "processing_reference", nullable = false, length = 100)
    private String processingReference;

    @Column(name = "date_state", nullable = false)
    private LocalDate dateState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_transfer_id")
    private PersonalAccount accountTransfer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_receive_id")
    private PersonalAccount accountReceive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_id")
    private Security security;

    @Column(name = "quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;

    @Column(name = "settlement_currency", nullable = false, length = 3)
    private String settlementCurrency;

    @Column(name = "settlement_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal settlementAmount;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
