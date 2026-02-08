package org.example.taskmanagementsystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.taskmanagementsystem.config.auth.TokenProvider;
import org.example.taskmanagementsystem.dto.auth.JwtDTO;
import org.example.taskmanagementsystem.dto.auth.LoginDTO;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthService service;
    @Autowired
    private TokenProvider tokenService;

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        return authHeader.replace("Bearer ", "");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid User data) {
        service.registerUser(data);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDTO> login(@RequestBody @Valid LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword());
        var authUser = authenticationManager.authenticate(usernamePassword);
        var accessToken = tokenService.generateAccessToken((User) authUser.getPrincipal());
        return ResponseEntity.ok(new JwtDTO(accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = recoverToken(request);

        if (service.invalidateToken(token)) {
            return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully, token invalidated.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logout failed, token already invalid.");
    }


}