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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                  @RequestParam(required = false) String description,
                                  @RequestParam String type,
                                  @RequestParam(required = false) Integer maxAttendees,
                                  @RequestParam(required = false) String location,
                                  @RequestParam(required = false, defaultValue = "CALENDAR_DAYS") String availabilityType,
                                  @RequestParam(required = false, defaultValue = "60") Integer availabilityValue,
                                  @RequestParam(required = false) String startDate,
                                  @RequestParam(required = false) String endDate,
                                  @RequestParam(required = false) boolean requestCalendarAccess,
                                  @AuthenticationPrincipal Object principal,
                                  RedirectAttributes redirectAttributes) {
        String email = extractEmail(principal);
        User user = userService.findByEmail(email);

        if (!user.isGoogleCalendarConnected() && !requestCalendarAccess) {
            redirectAttributes.addFlashAttribute("error", "You must approve Google Calendar access to create an event type.");
            return "redirect:/dashboard";
        }

        if (requestCalendarAccess && !user.isGoogleCalendarConnected()) {
            user.setGoogleCalendarConnected(true);
            userService.save(user); // Assuming save exists or we need to add it
        }

        EventType eventType = new EventType(name, duration, description, type, user);
        
        eventType.setLocation(location);
        eventType.setAvailabilityType(availabilityType);
        eventType.setAvailabilityValue(availabilityValue);

        if ("DATE_RANGE".equals(availabilityType) && startDate != null && endDate != null) {
            eventType.setStartDate(java.time.LocalDate.parse(startDate));
            eventType.setEndDate(java.time.LocalDate.parse(endDate));
        }
        
        if ("GROUP".equals(type) && maxAttendees != null) {
            eventType.setMaxAttendees(maxAttendees);
        }
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
