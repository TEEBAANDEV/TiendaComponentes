package com.example.ms_users.service;

import com.example.ms_users.model.User;
import com.example.ms_users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registro con rol específico
    public User register(String username, String password, String role) {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role != null ? role : "USER") // Por defecto USER
                .build();
        return userRepository.save(user);
    }
    public User register(String username, String password) {
        return register(username, password, "USER");
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}