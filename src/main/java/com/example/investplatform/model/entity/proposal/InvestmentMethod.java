package com.example.investplatform.model.entity.proposal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "investment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;
}
