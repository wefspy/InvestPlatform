package com.example.investplatform.model.entity.emitent;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmitentOkvedId implements Serializable {

    @Column(name = "emitent_id")
    private Long emitentId;

    @Column(name = "okved_id")
    private Integer okvedId;
}
