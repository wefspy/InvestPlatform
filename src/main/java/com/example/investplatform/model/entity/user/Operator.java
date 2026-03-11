package com.example.investplatform.model.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "operators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;

    @Column(name = "patronymic", length = 255)
    private String patronymic;
}
