package com.clone.calendly.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String slug; // Used for public profile: calendly.com/slug

    private String timezone;

    private int onboardingStep = 1;

    private String usageIntent;

    private String role;

    private boolean googleCalendarConnected = false;

    private boolean onboardingCompleted = false;

    public User(String fullName, String email, String password, String slug) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.slug = slug;
    }
}
