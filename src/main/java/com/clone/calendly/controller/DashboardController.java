package com.clone.calendly.controller;

import com.clone.calendly.model.EventType;
import com.clone.calendly.model.User;
import com.clone.calendly.repository.EventTypeRepository;
import com.clone.calendly.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final EventTypeRepository eventTypeRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal Object principal) {
        String email = extractEmail(principal);
        User user = userService.findByEmail(email);
        List<EventType> eventTypes = eventTypeRepository.findByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("eventTypes", eventTypes);
        return "dashboard";
    }

    @PostMapping("/event-types")
    public String createEventType(@RequestParam String name,
                                  @RequestParam Integer duration,
                                  @RequestParam String description,
                                  @RequestParam String color,
                                  @AuthenticationPrincipal Object principal) {
        String email = extractEmail(principal);
        User user = userService.findByEmail(email);
        EventType eventType = new EventType(name, duration, description, color, user);
        eventTypeRepository.save(eventType);
        return "redirect:/dashboard";
    }

    private String extractEmail(Object principal) {
        if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        throw new IllegalArgumentException("Unknown principal type");
    }
}
