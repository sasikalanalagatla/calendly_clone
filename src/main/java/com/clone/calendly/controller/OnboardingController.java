package com.clone.calendly.controller;

import com.clone.calendly.model.User;
import com.clone.calendly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/setup")
@RequiredArgsConstructor
public class OnboardingController {

    private final UserRepository userRepository;

    @GetMapping("/step1")
    public String step1(Model model, @AuthenticationPrincipal Object principal) {
        System.out.println("DEBUG: OnboardingController.step1 reached with principal: " + principal);
        User user = getCurrentUser(principal);
        if (user == null) {
            System.out.println("DEBUG: User not found for principal, redirecting to login");
            return "redirect:/login";
        }
        System.out.println("DEBUG: User found: " + user.getEmail() + ", Rendering step1");
        model.addAttribute("user", user);
        return "setup/step1";
    }

    @PostMapping("/step1")
    public String processStep1(@RequestParam String usageIntent, @AuthenticationPrincipal Object principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            user.setUsageIntent(usageIntent);
            user.setOnboardingStep(2);
            userRepository.save(user);
        }
        return "redirect:/setup/step2";
    }

    @GetMapping("/step2")
    public String step2(Model model, @AuthenticationPrincipal Object principal) {
        User user = getCurrentUser(principal);
        if (user == null) return "redirect:/login";
        
        model.addAttribute("user", user);
        return "setup/step2";
    }

    @PostMapping("/step2")
    public String processStep2(@RequestParam String role, @AuthenticationPrincipal Object principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            user.setRole(role);
            user.setOnboardingStep(3);
            userRepository.save(user);
        }
        return "redirect:/setup/step3";
    }

    @GetMapping("/step3")
    public String step3(Model model, @AuthenticationPrincipal Object principal) {
        User user = getCurrentUser(principal);
        if (user == null) return "redirect:/login";
        
        model.addAttribute("user", user);
        return "setup/step3";
    }

    @PostMapping("/step3")
    public String processStep3(@AuthenticationPrincipal Object principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            user.setOnboardingStep(3); // Stay on 3 until permissions handled
            userRepository.save(user);
        }
        return "redirect:/setup/permissions";
    }

    @GetMapping("/permissions")
    public String permissions(Model model, @AuthenticationPrincipal Object principal) {
        User user = getCurrentUser(principal);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "setup/permissions";
    }

    @PostMapping("/permissions")
    public String processPermissions(@AuthenticationPrincipal Object principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            // Ideally here you'd store that permissions were granted or trigger OAuth
            user.setOnboardingStep(4);
            userRepository.save(user);
        }
        return "redirect:/setup/step4";
    }

    @GetMapping("/step4")
    public String step4(Model model, @AuthenticationPrincipal Object principal) {
        User user = getCurrentUser(principal);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "setup/step4";
    }

    @PostMapping("/step4")
    public String processStep4(@AuthenticationPrincipal Object principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            user.setOnboardingStep(5);
            userRepository.save(user);
        }
        return "redirect:/setup/step5";
    }

    @GetMapping("/step5")
    public String step5(Model model, @AuthenticationPrincipal Object principal) {
        User user = getCurrentUser(principal);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "setup/step5";
    }

    @PostMapping("/step5")
    public String processStep5(@AuthenticationPrincipal Object principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            user.setOnboardingCompleted(true);
            user.setOnboardingStep(0); // Completed
            userRepository.save(user);
        }
        return "redirect:/dashboard";
    }

    private User getCurrentUser(Object principal) {
        String email = null;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        }
        
        if (email != null) {
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }
}
