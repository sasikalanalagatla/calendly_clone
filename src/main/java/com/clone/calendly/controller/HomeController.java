package com.clone.calendly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(org.springframework.ui.Model model) {
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDate firstOfMonth = now.withDayOfMonth(1);
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // 0=Sun, 1=Mon...
        int daysInMonth = now.lengthOfMonth();
        java.time.LocalDate lastDayOfPrevMonth = firstOfMonth.minusDays(1);
        int daysInPrevMonth = lastDayOfPrevMonth.lengthOfMonth();
        
        model.addAttribute("currentDate", now);
        model.addAttribute("firstDayOfWeek", firstDayOfWeek);
        model.addAttribute("daysInMonth", daysInMonth);
        model.addAttribute("daysInPrevMonth", daysInPrevMonth);
        model.addAttribute("monthName", now.getMonth().name());
        model.addAttribute("year", now.getYear());
        
        return "index";
    }
}
