package com.example.investplatform.model.entity.proposal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "proposal_document_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalDocumentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;
}
