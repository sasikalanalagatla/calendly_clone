package com.clone.calendly.controller;

import com.clone.calendly.model.EventType;
import com.clone.calendly.model.User;
import com.clone.calendly.repository.EventTypeRepository;
import com.clone.calendly.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
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
                                  @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        EventType eventType = new EventType(name, duration, description, color, user);
        eventTypeRepository.save(eventType);
        return "redirect:/dashboard";
    }
}
