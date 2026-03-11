package com.example.investplatform.model.entity.investor;

import com.example.investplatform.model.entity.account.PersonalAccount;
import com.example.investplatform.model.entity.user.User;
import com.example.investplatform.model.enums.InvestorType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "investors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Investor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "investor_type", nullable = false, length = 30)
    private InvestorType investorType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_account_id", nullable = false, unique = true)
    private PersonalAccount personalAccount;

    @Column(name = "is_qualified")
    private Boolean isQualified;

    @Column(name = "qualified_at")
    private LocalDateTime qualifiedAt;

    @Column(name = "qualified_basis", length = 500)
    private String qualifiedBasis;

    @Column(name = "risk_declaration_accepted")
    private Boolean riskDeclarationAccepted;

    @Column(name = "risk_accepted_at")
    private LocalDateTime riskAcceptedAt;

    @Version
    private Long version;
}
