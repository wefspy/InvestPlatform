package com.example.investplatform.model.entity.payment;

import com.example.investplatform.model.enums.ReceiptType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "yukassa_receipts")
public class YukassaReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "yukassa_receipt_id", nullable = false, unique = true, length = 50)
    private String yukassaReceiptId;

    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_type", nullable = false, length = 20)
    private ReceiptType receiptType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_id")
    private PaymentRefund refund;

    @Column(name = "yukassa_status", nullable = false, length = 30)
    private String yukassaStatus;

    @Column(name = "fiscal_document_number", length = 50)
    private String fiscalDocumentNumber;

    @Column(name = "fiscal_storage_number", length = 50)
    private String fiscalStorageNumber;

    @Column(name = "fiscal_attribute", length = 50)
    private String fiscalAttribute;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "receipt_url", length = 1024)
    private String receiptUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
