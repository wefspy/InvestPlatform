package com.example.investplatform.model.entity.emitent;

import com.example.investplatform.model.enums.PersonType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "emitent_le_governing_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitentLeGoverningMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "governing_body_id", nullable = false)
    private EmitentLeGoverningBody governingBody;

    @Enumerated(EnumType.STRING)
    @Column(name = "person_type", nullable = false, length = 20)
    private PersonType personType;

    @Column(name = "full_name", nullable = false, length = 500)
    private String fullName;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "inn", nullable = false, length = 12)
    private String inn;

    @Column(name = "ogrn", length = 13)
    private String ogrn;

    @Column(name = "foreign_registration_info", columnDefinition = "text")
    private String foreignRegistrationInfo;
}
