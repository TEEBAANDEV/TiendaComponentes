package com.example.ms_users.service;
import com.example.ms_users.model.User;
import com.example.ms_users.repository.UserRepository;
import com.example.ms_users.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Given datos válidos, When se registra con rol explícito, Then cifra la clave y persiste el usuario")
    void deberiaRegistrarUsuarioConRol() {
        // Given
        String rawPassword = "password123";
        String encodedPassword = "encoded_password_abc";
        User mockUser = User.builder().username("isa_user").password(encodedPassword).role("ADMIN").direccion("Chile").build();

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        User resultado = userService.register("isa_user", rawPassword, "ADMIN", "Chile");

        // Then
        assertNotNull(resultado);
        assertEquals(encodedPassword, resultado.getPassword());
        assertEquals("ADMIN", resultado.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Given datos sin rol, When se registra por el método sobrecargado, Then asigna el rol USER por defecto")
    void deberiaRegistrarUsuarioConRolPorDefecto() {
        // Given
        String rawPassword = "password123";
        String encodedPassword = "encoded_password_abc";
        User mockUser = User.builder().username("pedro").password(encodedPassword).role("USER").direccion("Santiago").build();

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        User resultado = userService.register("pedro", rawPassword, "Santiago");

        // Then
        assertNotNull(resultado);
        assertEquals("USER", resultado.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Given un username existente, When se busca por username, Then retorna el Optional con la entidad")
    void deberiaBuscarPorUsernameExistente() {
        // Given
        String username = "isa_user";
        User user = User.builder().username(username).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        Optional<User> resultado = userService.findByUsername(username);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(username, resultado.get().getUsername());
    }

    @Test
    @DisplayName("Given un ID de usuario en la BD, When se busca por ID, Then retorna el Optional correcto")
    void deberiaBuscarPorIdExistente() {
        // Given
        Long id = 10L;
        User user = User.builder().id(id).username("isa_user").build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        Optional<User> resultado = userService.findById(id);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
    }

    @Test
    @DisplayName("Given un ID que no figura en los registros, When se busca por ID, Then retorna un Optional vacío")
    void deberiaRetornarVacioCuandoIdNoExiste() {
        // Given
        Long id = 999L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<User> resultado = userService.findById(id);

        // Then
        assertFalse(resultado.isPresent());
    }
}