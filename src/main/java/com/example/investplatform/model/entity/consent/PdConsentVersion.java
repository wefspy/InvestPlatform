package com.example.investplatform.model.entity.consent;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pd_consent_versions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"consent_type_id", "version_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PdConsentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_type_id", nullable = false)
    private PdConsentType consentType;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
