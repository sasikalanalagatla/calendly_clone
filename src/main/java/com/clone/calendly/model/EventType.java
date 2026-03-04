package com.clone.calendly.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "event_types")
@Getter
@Setter
@NoArgsConstructor
public class EventType extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer duration; // in minutes

    private String description;

    @Column(nullable = false)
    private String color; // hex code or identifier

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public EventType(String name, Integer duration, String description, String color, User user) {
        this.name = name;
        this.duration = duration;
        this.description = description;
        this.color = color;
        this.user = user;
    }
}
