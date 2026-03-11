package com.example.investplatform.model.entity.investor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "investor_document_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestorDocumentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;
}
