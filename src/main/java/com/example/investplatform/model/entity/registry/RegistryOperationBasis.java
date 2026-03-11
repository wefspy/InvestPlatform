package com.example.investplatform.model.entity.registry;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "registry_operation_basis")
public class RegistryOperationBasis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registry_operation_id", nullable = false)
    private RegistryOperation registryOperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_type_id", nullable = false)
    private ContractType contractType;

    @Column(name = "contract_narrative", length = 500)
    private String contractNarrative;

    @Column(name = "doc_num", nullable = false, length = 100)
    private String docNum;

    @Column(name = "doc_date", nullable = false)
    private LocalDate docDate;
}
