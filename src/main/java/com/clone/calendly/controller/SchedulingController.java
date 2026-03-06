package com.clone.calendly.controller;

import com.clone.calendly.model.User;
import com.clone.calendly.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SchedulingController {

    private final UserService userService;

    @GetMapping("/scheduling")
    public String scheduling(Model model, @AuthenticationPrincipal Object principal) {
        if (principal != null) {
            String email = extractEmail(principal);
            User user = userService.findByEmail(email);
            model.addAttribute("user", user);
            model.addAttribute("authenticated", true);
        } else {
            model.addAttribute("authenticated", false);
        }
        return "scheduling";
    }

    private String extractEmail(Object principal) {
        if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }
}
