package com.example.investplatform.model.entity.security;

import com.example.investplatform.model.entity.account.PersonalAccount;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "security_balances", uniqueConstraints = @UniqueConstraint(columnNames = {"personal_account_id", "security_id"}))
public class SecurityBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_account_id", nullable = false)
    private PersonalAccount personalAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_id", nullable = false)
    private Security security;

    @Column(name = "quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;

    @Column(name = "quantity_blocked", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantityBlocked;

    @Column(name = "quantity_pledged", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantityPledged;

    @Column(name = "quantity_encumbered", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantityEncumbered;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
