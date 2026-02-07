package org.example.taskmanagementsystem.service;

import org.example.taskmanagementsystem.exception.DuplicateEmailException;
import org.example.taskmanagementsystem.model.InvalidToken;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.repository.InvalidTokenRepository;
import org.example.taskmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvalidTokenRepository invalidTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new DuplicateEmailException("User already exists");
        }

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    //  Blacklist token on logout
    public boolean invalidateToken(String token) {
        if (token != null && invalidTokenRepository.findByToken(token).isEmpty()) {
            InvalidToken invalidToken = new InvalidToken(token);
            invalidTokenRepository.save(invalidToken);
            return true;
        }

        return false;
    }
}