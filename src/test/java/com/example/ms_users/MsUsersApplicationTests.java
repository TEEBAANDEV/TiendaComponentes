package com.example.ms_users.service;

import com.example.ms_users.model.User;
import com.example.ms_users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder; // O la librería de PasswordEncoder que uses

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MsUsersApplicationTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegisterUsuarioConRolPorDefectoExitoso() {
        String username = "shamo_dev";
        String passwordPlana = "miPassword123";
        String passwordEncriptada = "encodedPassword123_mock";
        String direccion = "123 Main St, Springfield";

        when(passwordEncoder.encode(passwordPlana)).thenReturn(passwordEncriptada);

        User usuarioGuardado = User.builder()
                .id(1L)
                .username(username)
                .password(passwordEncriptada)
                .role("USER")
                .direccion(direccion)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(usuarioGuardado);

        User resultado = userService.register(username, passwordPlana, null, direccion);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(passwordEncriptada, resultado.getPassword());
        assertEquals("USER", resultado.getRole()); // Validamos que la lógica del operador ternario funcionó

        verify(passwordEncoder, times(1)).encode(passwordPlana);
        verify(userRepository, times(1)).save(any(User.class));
    }
}