package com.clone.calendly.service;

import com.clone.calendly.model.User;
import com.clone.calendly.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // @Lazy breaks the circular dependency between SecurityConfig and this service.
    // CustomOAuth2UserService needs PasswordEncoder from SecurityConfig.
    // SecurityConfig needs CustomOAuth2UserService for its oauth2Login configuration.
    public CustomOAuth2UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String clientRegistrationId = userRequest.getClientRegistration().getRegistrationId();
        
        if ("google".equalsIgnoreCase(clientRegistrationId)) {
            processOAuth2User(oAuth2User);
        }
        
        return oAuth2User;
    }

    private void processOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }
        
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            // Register new user automatically
            User user = new User();
            user.setEmail(email);
            user.setFullName(name != null ? name : email.split("@")[0]);
            
            // Generate a random password for OAuth2 users since they won't use it to log in,
            // but the database requires it to not be null.
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            
            // Generate a basic slug based on full name or email prefix
            String baseSlug = user.getFullName().toLowerCase().replaceAll("[^a-z0-9]", "-");
            String uniqueSlug = baseSlug;
            int counter = 1;
            
            while (userRepository.findBySlug(uniqueSlug).isPresent()) {
                uniqueSlug = baseSlug + "-" + counter++;
            }
            
            user.setSlug(uniqueSlug);
            userRepository.save(user);
        }
    }
}
