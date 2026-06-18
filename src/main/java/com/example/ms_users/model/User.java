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
@Schema(description = "Entity representing a User in the system")
public class User extends RepresentationModel<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @Column(nullable = false)
    @Schema(description = "Password of the user", example = "encodedPassword123")
    private String password;

    @Column(nullable = false)
    @Schema(description = "User role", example = "USER")
    private String role;

    @Column(nullable = false)
    @Schema(description = "Delivery address of the user", example = "123 Main St, Springfield")
    private String direccion;
}
