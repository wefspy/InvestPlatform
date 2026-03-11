package com.example.investplatform.model.entity.emitent;

import com.example.investplatform.model.entity.account.PersonalAccount;
import com.example.investplatform.model.entity.user.User;
import com.example.investplatform.model.enums.EmitentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "emitents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emitent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "emitent_type", nullable = false, length = 30)
    private EmitentType emitentType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_account_id", nullable = false, unique = true)
    private PersonalAccount personalAccount;
}
