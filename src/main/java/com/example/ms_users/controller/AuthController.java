package com.example.ms_users.controller;

import com.example.ms_users.model.User;
import com.example.ms_users.security.jwt.JwtService;
import com.example.ms_users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Controlador de Autenticación", description = "Endpoints para el registro, inicio de sesión y validación de tokens de usuario")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService, UserService userService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema con rol especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, campos faltantes o inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto, el usuario ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al intentar registrar")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    example = "{\n  \"username\": \"john_doe\",\n  \"password\": \"encodedPassword123\",\n  \"role\": \"USER\",\n  \"direccion\": \"123\"\n}"
                            )
                    )
            )
            @RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            String role = body.getOrDefault("role", "USER"); // Por defecto USER
            String direccion = body.get("direccion");

            if (username == null || password == null || username.isBlank() || password.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username y password son requeridos"));
            }

            // Validar rol
            if (!role.equals("USER") && !role.equals("ADMIN")) {
                role = "USER";
            }

            userService.register(username, password, role, direccion);
            return ResponseEntity.ok(Map.of(
                    "message", "Usuario registrado correctamente",
                    "role", role
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El usuario ya existe"));
        }
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y retorna un token JWT válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sesión iniciada correctamente, retorna token JWT"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    example = "{\n  \"username\": \"john_doe\",\n  \"password\": \"encodedPassword123\"\n}"
                            )
                    )
            )
            @RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");

            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            if (auth.isAuthenticated()) {
                // Obtener rol del usuario
                User user = userService.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                String token = jwtService.generateToken(username, user.getRole());

                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "username", username,
                        "role", user.getRole() // Devolver rol
                ));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario o contraseña incorrectos"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar la solicitud"));
        }
    }

    @Operation(summary = "Validar token JWT", description = "Verifica si el token enviado es válido y no ha expirado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token válido"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        return ResponseEntity.ok(Map.of("valid", true));
    }
}

