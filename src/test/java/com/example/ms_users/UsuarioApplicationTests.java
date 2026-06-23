package com.example.ms_users;

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
class UsuarioApplicationTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Debería registrar un usuario con rol USER por defecto si no se especifica un rol")
    void testRegisterUsuarioConRolPorDefectoExitoso() {
        String username = "dev";
        String passwordPlana = "miPassword123";
        String passwordEncriptada = "encodedPassword123_mock";
        String direccion = "duoc, Chile";

        User usuarioSimulado = User.builder()
                .id(1L)
                .username(username)
                .password(passwordEncriptada)
                .role("USER")
                .direccion(direccion)
                .build();

        when(passwordEncoder.encode(passwordPlana)).thenReturn(passwordEncriptada);
        when(userRepository.save(any(User.class))).thenReturn(usuarioSimulado);

        User resultado = userService.register(username, passwordPlana, null, direccion);

        assertNotNull(resultado, "El usuario guardado no debería ser nulo");
        assertEquals(1L, resultado.getId());
        assertEquals(passwordEncriptada, resultado.getPassword());
        assertEquals("USER", resultado.getRole(), "Se debió asignar el rol por defecto USER");

        verify(passwordEncoder, times(1)).encode(passwordPlana);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debería registrar un usuario con un rol específico si este es proporcionado")
    void testRegisterUsuarioConRolEspecifico() {
        String username = "admin_user";
        String passwordPlana = "admin123";
        String passwordEncriptada = "adminEncrypted_mock";
        String rolEspecifico = "ADMIN";
        String direccion = "Santiago, Chile";

        User usuarioSimulado = User.builder()
                .id(2L)
                .username(username)
                .password(passwordEncriptada)
                .role(rolEspecifico)
                .direccion(direccion)
                .build();

        when(passwordEncoder.encode(passwordPlana)).thenReturn(passwordEncriptada);
        when(userRepository.save(any(User.class))).thenReturn(usuarioSimulado);

        User resultado = userService.register(username, passwordPlana, rolEspecifico, direccion);

        assertNotNull(resultado);
        assertEquals(rolEspecifico, resultado.getRole(), "El rol asignado debería ser ADMIN");

        verify(passwordEncoder, times(1)).encode(passwordPlana);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debería retornar un Optional vacío si el usuario buscado por ID no existe")
    void deberiaRetornarUsuarioVacioCuandoNoExiste() {
        Long idInexistente = 99L;
        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        Optional<User> resultado = userService.findById(idInexistente);

        assertTrue(resultado.isEmpty(), "El resultado debería ser un Optional vacío");
        verify(userRepository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Debería retornar el usuario correspondiente cuando el ID sí existe")
    void deberiaRetornarUsuarioCuandoExiste() {
        Long idExistente = 1L;
        User usuarioSimulado = User.builder()
                .id(idExistente)
                .username("usuario_test")
                .role("USER")
                .build();

        when(userRepository.findById(idExistente)).thenReturn(Optional.of(usuarioSimulado));
        Optional<User> resultado = userService.findById(idExistente);

        assertTrue(resultado.isPresent(), "El Optional debería contener un usuario");
        assertEquals("usuario_test", resultado.get().getUsername());
        verify(userRepository, times(1)).findById(idExistente);
    }
}