package com.example.ms_users.controller;

import com.example.ms_users.model.User;
import com.example.ms_users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Controller", description = "Endpoints para la gestión y consulta de perfiles de usuario")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Obtener un usuario por su ID único", description = "Proporciona información detallada del usuario junto con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente"),
            @ApiResponse(responseCode = "400", description = "Formato de ID proporcionado inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al procesar la consulta")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        log.info("Fetching user with ID: {}", id);
        return userService.findById(id)
                .map(user -> {
                    user.add(WebMvcLinkBuilder.linkTo(
                            WebMvcLinkBuilder.methodOn(UserController.class).getUserById(id)
                    ).withSelfRel());
                    log.info("Successfully found user with ID: {}", id);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    log.warn("User with ID: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
