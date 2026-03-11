package com.example.investplatform.model.entity.registry;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "registry_operation_documents")
public class RegistryOperationDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registry_operation_id")
    private RegistryOperation registryOperation;

    @Column(name = "in_doc_num", nullable = false, length = 100)
    private String inDocNum;

    @Column(name = "in_reg_date", nullable = false)
    private LocalDateTime inRegDate;

    @Column(name = "out_doc_num", length = 100)
    private String outDocNum;

    @Column(name = "out_doc_date")
    private LocalDateTime outDocDate;
}
