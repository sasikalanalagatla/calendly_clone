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
    private String type = "ONE_ON_ONE"; // ONE_ON_ONE, GROUP, COLLECTIVE, POLL

    private Integer maxAttendees; // Only for GROUP meetings

    private String location; // PHONE, GOOGLE_MEET, ZOOM, IN_PERSON

    private String availabilityType = "CALENDAR_DAYS"; // CALENDAR_DAYS, DATE_RANGE, INDEFINITE
    private Integer availabilityValue = 60; // e.g., 60 days
    
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public EventType(String name, Integer duration, String description, String type, User user) {
        this.name = name;
        this.duration = duration;
        this.description = description;
        this.type = type;
        this.user = user;
    }
}
