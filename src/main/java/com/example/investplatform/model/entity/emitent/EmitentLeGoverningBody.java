package com.example.investplatform.model.entity.emitent;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "emitent_le_governing_bodies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitentLeGoverningBody {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emitent_legal_entity_id", nullable = false)
    private EmitentLegalEntity emitentLegalEntity;

    @Column(name = "body_name", nullable = false, length = 255)
    private String bodyName;
}
