package com.example.investplatform.model.entity.notification;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_channels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;
}
