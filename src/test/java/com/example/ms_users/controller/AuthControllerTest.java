package com.example.ms_users.controller;
import com.example.ms_users.controller.AuthController;
import com.example.ms_users.model.User;
import com.example.ms_users.security.jwt.JwtService;
import com.example.ms_users.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("Given datos de registro correctos, When se procesa la solicitud, Then registra al usuario con éxito y retorna 200 OK")
    void deberiaRegistrarExitosamente() {
        // Given
        Map<String, String> body = Map.of("username", "isa", "password", "pass123", "role", "USER", "direccion", "Chile");
        when(userService.register(anyString(), anyString(), anyString(), anyString())).thenReturn(new User());

        // When
        ResponseEntity<?> response = authController.register(body);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Given un payload con campos vacíos o nulos, When se registra, Then detiene el flujo y responde 400 Bad Request")
    void deberiaRetornarBadRequestCuandoFaltanCampos() {
        // Given
        Map<String, String> body = Map.of("username", "  ", "password", "");

        // When
        ResponseEntity<?> response = authController.register(body);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService, never()).register(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Given un rol no soportado por el negocio, When se registra, Then fuerza el rol a USER por defecto y responde 200 OK")
    void deberiaNormalizarRolInvalidoAUser() {
        // Given
        Map<String, String> body = Map.of("username", "lucas", "password", "pass", "role", "SUPER_ADMIN_MALO");
        when(userService.register(eq("lucas"), eq("pass"), eq("USER"), any())).thenReturn(new User());

        // When
        ResponseEntity<?> response = authController.register(body);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Given credenciales correctas, When se inicia sesión, Then genera el token JWT y responde 200 OK")
    void deberiaIniciarSesionCorrectamente() {
        // Given
        Map<String, String> body = Map.of("username", "isa", "password", "pass123");
        Authentication authMock = mock(Authentication.class);
        User userMock = User.builder().username("isa").role("USER").build();

        when(authMock.isAuthenticated()).thenReturn(true);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(userService.findByUsername("isa")).thenReturn(Optional.of(userMock));
        when(jwtService.generateToken("isa", "USER")).thenReturn("jwt_token_valido");

        // When
        ResponseEntity<?> response = authController.login(body);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Given una contraseña errónea, When se intenta loguear, Then captura BadCredentialsException y responde 401 Unauthorized")
    void deberiaRetornar401AnteCredencialesIncorrectas() {
        // Given
        Map<String, String> body = Map.of("username", "isa", "password", "incorrecta");
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid password"));

        // When
        ResponseEntity<?> response = authController.login(body);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}