package com.ecommerce.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.auth.dto.LoginRequest;
import com.ecommerce.auth.dto.LoginResponse;
import com.ecommerce.auth.dto.MessageResponse;
import com.ecommerce.auth.dto.SignupRequest;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.auth.util.JwtUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    try {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), 
                        loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        //debug logging
        System.out.println("Authentication successful for: " + loginRequest.getUsername());
        
        String jwt = jwtUtils.generateJwtToken(authentication);
        System.out.println("JWT generated: " + jwt.substring(0, 20) + "...");

        org.springframework.security.core.userdetails.User userPrincipal = 
            (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        return ResponseEntity.ok(new LoginResponse(jwt, userPrincipal.getUsername()));
        
    } catch (org.springframework.security.core.AuthenticationException e) {
        //only catches authentication failures
        System.err.println("Authentication failed: " + e.getMessage());
        return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Invalid username or password"));
    } catch (Exception e) {
        System.err.println("Other error during signin: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500)
                .body(new MessageResponse("Error: Internal server error"));
    }
}

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        //create new users account
        User user = new User(signUpRequest.getUsername(),
                           signUpRequest.getEmail(),
                           encoder.encode(signUpRequest.getPassword()));

        try {
            userRepository.save(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Could not create user"));
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    
}
