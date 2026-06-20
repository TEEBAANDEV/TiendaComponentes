package com.example.ms_users.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@Schema(description = "Entidad que representa a un Usuario en el sistema")
public class User extends RepresentationModel<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "Nombre de usuario", example = "john_doe")
    private String username;

    @Column(nullable = false)
    @Schema(description = "Contraseña del usuario", example = "encodedPassword123")
    private String password;

    @Column(nullable = false)
    @Schema(description = "Rol del usuario", example = "USER")
    private String role;

    @Column(nullable = false)
    @Schema(description = "Dirección de entrega del usuario", example = "123 Main St, Springfield")
    private String direccion;
}
