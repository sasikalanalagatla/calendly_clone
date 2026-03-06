package com.clone.calendly.service;

import com.clone.calendly.model.User;
import com.clone.calendly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String fullName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Simple slug generation: handle-name-123
        String slug = fullName.toLowerCase().replaceAll("[^a-z0-0]", "-");
        if (userRepository.findBySlug(slug).isPresent()) {
            slug += "-" + (int)(Math.random() * 1000);
        }

        User user = new User(fullName, email, passwordEncoder.encode(password), slug);
        user.setTimezone("UTC"); // Default
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}
