package com.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chord_views")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChordView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chord_id", nullable = false)
    private Chord chord;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @PrePersist
    public void prePersist() {
        viewedAt = LocalDateTime.now();
    }
}