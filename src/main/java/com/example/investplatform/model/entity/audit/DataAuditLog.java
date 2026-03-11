package com.example.investplatform.model.entity.audit;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "data_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name", nullable = false, length = 100)
    private String tableName;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @Column(name = "action", nullable = false, length = 10)
    private String action;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_values", columnDefinition = "jsonb")
    private Map<String, Object> oldValues;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_values", columnDefinition = "jsonb")
    private Map<String, Object> newValues;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "changed_fields", columnDefinition = "text[]")
    private String[] changedFields;

    @Column(name = "db_user", nullable = false, length = 100)
    private String dbUser;

    @Column(name = "app_user_id")
    private Long appUserId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
