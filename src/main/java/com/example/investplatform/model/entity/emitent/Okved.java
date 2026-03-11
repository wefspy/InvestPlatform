package com.example.investplatform.model.entity.emitent;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "okveds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Okved {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "name", nullable = false, unique = true, length = 500)
    private String name;
}
