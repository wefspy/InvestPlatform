package com.example.investplatform.model.entity.contract;

import com.example.investplatform.model.entity.investor.Investor;
import com.example.investplatform.model.entity.payment.Payment;
import com.example.investplatform.model.entity.payment.PaymentRefund;
import com.example.investplatform.model.entity.proposal.InvestmentProposal;
import com.example.investplatform.model.entity.security.Security;
import com.example.investplatform.model.entity.user.Operator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_contracts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"proposal_id", "investor_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_number", nullable = false, unique = true, length = 100)
    private String contractNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private InvestmentProposal proposal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id", nullable = false)
    private Investor investor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ContractStatus status;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_id")
    private Security security;

    @Column(name = "securities_quantity", precision = 18, scale = 4)
    private BigDecimal securitiesQuantity;

    @Column(name = "price_per_security", precision = 18, scale = 4)
    private BigDecimal pricePerSecurity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Operator reviewedBy;

    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason;

    @Column(name = "withdrawal_reason", columnDefinition = "text")
    private String withdrawalReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_id")
    private PaymentRefund refund;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locked_by")
    private Operator lockedBy;

    @Column(name = "lock_heartbeat_at")
    private LocalDateTime lockHeartbeatAt;

    @Version
    private Long version;
}
