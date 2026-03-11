package com.example.investplatform.model.entity.emitent;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "emitent_okved")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmitentOkved {

    @EmbeddedId
    private EmitentOkvedId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("emitentId")
    @JoinColumn(name = "emitent_id")
    private Emitent emitent;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("okvedId")
    @JoinColumn(name = "okved_id")
    private Okved okved;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;
}
