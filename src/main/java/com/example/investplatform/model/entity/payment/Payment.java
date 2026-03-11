package com.example.investplatform.model.entity.payment;

import com.example.investplatform.model.entity.account.PersonalAccount;
import com.example.investplatform.model.enums.PaymentDirection;
import com.example.investplatform.model.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
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
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "yukassa_payment_id", nullable = false, unique = true, length = 50)
    private String yukassaPaymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_account_id", nullable = false)
    private PersonalAccount personalAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 30)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 10)
    private PaymentDirection direction;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "yukassa_status", nullable = false, length = 50)
    private String yukassaStatus;

    @Column(name = "payment_method_type", nullable = false, length = 50)
    private String paymentMethodType;

    @Column(name = "description", length = 500)
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "yukassa_metadata", columnDefinition = "jsonb")
    private Map<String, Object> yukassaMetadata;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 255)
    private String idempotencyKey;

    @Column(name = "receipt_url", unique = true, length = 1024)
    private String receiptUrl;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
