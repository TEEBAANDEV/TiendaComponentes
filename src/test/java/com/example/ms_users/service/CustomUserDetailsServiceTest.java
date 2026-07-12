package com.example.ms_users.service;


import com.example.ms_users.model.User;
import com.example.ms_users.repository.UserRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Given un username existente, When se solicita UserDetails, Then retorna la instancia mapeada con el prefijo ROLE_")
    void deberiaCargarUserDetailsCorrectamente() {
        // Given
        String username = "john_doe";
        User userEntity = User.builder()
                .username(username)
                .password("encoded_pass")
                .role("USER")
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Then
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Given un username inexistente, When se solicita UserDetails, Then lanza una excepción UsernameNotFoundException")
    void deberiaLanzarExcepcionCuandoNoExisteElUsuario() {
        // Given
        String username = "missing_user";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
    }
}