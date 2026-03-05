package com.clone.calendly.controller;

import com.clone.calendly.model.EventType;
import com.clone.calendly.model.User;
import com.clone.calendly.repository.EventTypeRepository;
import com.clone.calendly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class PublicBookingController {

    private final UserRepository userRepository;
    private final EventTypeRepository eventTypeRepository;

    @GetMapping("/{userSlug}/{eventTypeId}")
    public String publicBookingPage(@PathVariable String userSlug,
                                    @PathVariable Long eventTypeId,
                                    Model model) {
        User user = userRepository.findBySlug(userSlug)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        EventType eventType = eventTypeRepository.findById(eventTypeId)
            .filter(et -> et.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("Event type not found"));

        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDate firstOfMonth = now.withDayOfMonth(1);
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        
        model.addAttribute("user", user);
        model.addAttribute("eventType", eventType);
        model.addAttribute("currentDate", now);
        model.addAttribute("firstDayOfWeek", firstDayOfWeek);
        model.addAttribute("daysInMonth", now.lengthOfMonth());
        
        return "booking-page";
    }

    @GetMapping("/{userSlug}")
    public String userPublicProfile(@PathVariable String userSlug, Model model) {
        User user = userRepository.findBySlug(userSlug)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        model.addAttribute("eventTypes", eventTypeRepository.findByUser(user));
        return "user-profile";
    }
}
