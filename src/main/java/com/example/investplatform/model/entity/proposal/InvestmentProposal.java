package com.example.investplatform.model.entity.proposal;

import com.example.investplatform.model.entity.emitent.Emitent;
import com.example.investplatform.model.entity.security.Security;
import com.example.investplatform.model.entity.user.Operator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_proposals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emitent_id", nullable = false)
    private Emitent emitent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ProposalStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investment_method_id", nullable = false)
    private InvestmentMethod investmentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_id")
    private Security security;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "investment_goals", nullable = false, columnDefinition = "text")
    private String investmentGoals;

    @Column(name = "goal_risk_factors", nullable = false, columnDefinition = "text")
    private String goalRiskFactors;

    @Column(name = "emitent_risks", nullable = false, columnDefinition = "text")
    private String emitentRisks;

    @Column(name = "investment_risks", nullable = false, columnDefinition = "text")
    private String investmentRisks;

    @Column(name = "issue_decision_info", columnDefinition = "text")
    private String issueDecisionInfo;

    @Column(name = "placement_procedure", columnDefinition = "text")
    private String placementProcedure;

    @Column(name = "placement_terms", columnDefinition = "text")
    private String placementTerms;

    @Column(name = "placement_conditions", columnDefinition = "text")
    private String placementConditions;

    @Column(name = "has_preemptive_right")
    private Boolean hasPreemptiveRight;

    @Column(name = "preemptive_right_details", columnDefinition = "text")
    private String preemptiveRightDetails;

    @Column(name = "risk_warning", nullable = false, columnDefinition = "text")
    private String riskWarning;

    @Column(name = "max_investment_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal maxInvestmentAmount;

    @Column(name = "min_investment_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal minInvestmentAmount;

    @Column(name = "proposal_start_date", nullable = false)
    private LocalDate proposalStartDate;

    @Column(name = "proposal_end_date", nullable = false)
    private LocalDate proposalEndDate;

    @Column(name = "essential_contract_terms", nullable = false, columnDefinition = "text")
    private String essentialContractTerms;

    @Column(name = "expert_monitoring_info", columnDefinition = "text")
    private String expertMonitoringInfo;

    @Column(name = "has_property_rights_condition", nullable = false)
    private Boolean hasPropertyRightsCondition;

    @Column(name = "property_rights_details", columnDefinition = "text")
    private String propertyRightsDetails;

    @Column(name = "applicable_law", nullable = false, length = 500)
    private String applicableLaw;

    @Column(name = "suspensive_conditions", columnDefinition = "text")
    private String suspensiveConditions;

    @Column(name = "collected_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal collectedAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Operator reviewedBy;

    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locked_by")
    private Operator lockedBy;

    @Column(name = "lock_heartbeat_at")
    private LocalDateTime lockHeartbeatAt;

    @Version
    private Long version;
}
