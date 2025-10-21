package br.com.devpoint.taskflow.authservice.controller;


import br.com.devpoint.taskflow.authservice.dto.AuthResponse;
import br.com.devpoint.taskflow.authservice.dto.LoginRequest;
import br.com.devpoint.taskflow.authservice.dto.RegistrationRequest;
import br.com.devpoint.taskflow.authservice.model.User;
import br.com.devpoint.taskflow.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping("/health")
    @Operation(summary = "Health check endpoint")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Auth service is healthy");
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {

        AuthResponse response = authService.registerUser(registrationRequest);
        return ResponseEntity.ok(response);
    }
}
