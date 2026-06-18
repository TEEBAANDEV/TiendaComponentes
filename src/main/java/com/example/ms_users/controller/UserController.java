package com.example.ms_users.controller;

import com.example.ms_users.model.User;
import com.example.ms_users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class UserController {

    private final UserService userService;

    @Operation(summary = "Retrieve a user by their unique ID", description = "Provides detailed user info along with HATEOAS links.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("{id}")
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
