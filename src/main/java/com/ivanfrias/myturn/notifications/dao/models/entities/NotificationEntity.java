package com.ivanfrias.myturn.notifications.dao.models.entities;

import com.ivanfrias.myturn.notifications.dao.models.enums.NotificationTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false)
    private String emailTo;

    @Column(nullable = false, unique = false)
    private String subject;

    @Column(nullable = false, unique = false)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationTypeEnum notificationType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_sended")
    private Boolean isSended;

    @Generated
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @Generated
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
