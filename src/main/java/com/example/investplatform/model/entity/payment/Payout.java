package com.example.investplatform.model.entity.payment;

import com.example.investplatform.model.entity.account.PersonalAccount;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payouts")
public class Payout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "yukassa_payout_id", nullable = false, unique = true, length = 50)
    private String yukassaPayoutId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_account_id", nullable = false)
    private PersonalAccount personalAccount;

    @Column(name = "payout_destination_type", nullable = false, length = 50)
    private String payoutDestinationType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "destination_details", columnDefinition = "jsonb")
    private Map<String, Object> destinationDetails;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "yukassa_status", nullable = false, length = 50)
    private String yukassaStatus;

    @Column(name = "description", length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 255)
    private String idempotencyKey;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Version
    private Long version;
}
