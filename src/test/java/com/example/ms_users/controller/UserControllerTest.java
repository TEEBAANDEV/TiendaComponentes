package com.example.ms_users.controller;

import com.example.ms_users.controller.UserController;
import com.example.ms_users.model.User;
import com.example.ms_users.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("Given un ID de usuario existente, When se llama por URL, Then retorna 200 OK con enlaces relacionales HATEOAS")
    void deberiaObtenerUsuarioPorId() {
        // Given
        Long id = 10L;
        User user = User.builder().id(id).username("Isa").build();
        when(userService.findById(id)).thenReturn(Optional.of(user));

        // When
        ResponseEntity<User> response = userController.getUserById(id);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getLinks().isEmpty());
    }

    @Test
    @DisplayName("Given un ID inexistente en la base de datos, When se consulta, Then retorna un estado 404 Not Found")
    void deberiaRetornar404CuandoUsuarioNoExiste() {
        // Given
        Long id = 999L;
        when(userService.findById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<User> response = userController.getUserById(id);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Given un ID límite o cero, When se consulta el perfil, Then retorna igualmente 404 de manera controlada")
    void deberiaManejarIdsFrontera() {
        // Given
        Long id = 0L;
        when(userService.findById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<User> response = userController.getUserById(id);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Given una caída de la base de datos, When se busca el usuario, Then se propaga la excepción hacia arriba")
    void deberiaLanzarExcepcionCuandoElServicioFalla() {
        // Given
        Long id = 1L;
        when(userService.findById(id)).thenThrow(new RuntimeException("Database Connection Timeout"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userController.getUserById(id);
        });
    }

    @Test
    @DisplayName("Given una petición válida, When el servicio retorna el usuario, Then el Self Link debe apuntar exactamente a su recurso")
    void deberiaVerificarEstructuraDelLinkHateoas() {
        // Given
        Long id = 55L;
        User user = User.builder().id(id).username("Marta").build();
        when(userService.findById(id)).thenReturn(Optional.of(user));

        // When
        ResponseEntity<User> response = userController.getUserById(id);

        // Then
        assertNotNull(response.getBody());
        assertEquals("self", response.getBody().getLink("self").get().getRel().value());
        assertTrue(response.getBody().getLink("self").get().getHref().contains("/api/v1/users/55"));
    }
}