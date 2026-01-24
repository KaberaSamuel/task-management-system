package org.example.taskmanagementsystem.service;

import org.example.taskmanagementsystem.dto.UserDTO;
import org.example.taskmanagementsystem.exception.ResourceNotFoundException;
import org.example.taskmanagementsystem.exception.DuplicateEmailException;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all users
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    // Get user by id
    public Optional<UserDTO> getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
        return Optional.of(UserDTO.fromUser(user));
    }

    // Create a new user
    public UserDTO createUser(User user) {
        // check if user isn't a duplicate
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("User with same email already exists");
        }

        User savedUser = userRepository.save(user);
        return UserDTO.fromUser(savedUser);
    }

    // Update an existing user
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setRole(userDTO.getRole());
        User updatedUser = userRepository.save(existingUser);
        return UserDTO.fromUser(updatedUser);
    }

    // Delete a user by id
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("User with id: " + id + " not found");
        }
    }
}
