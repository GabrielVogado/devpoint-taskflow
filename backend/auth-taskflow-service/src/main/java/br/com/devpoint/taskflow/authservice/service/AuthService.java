package br.com.devpoint.taskflow.authservice.service;

import br.com.devpoint.taskflow.authservice.dto.AuthResponse;
import br.com.devpoint.taskflow.authservice.dto.LoginRequest;
import br.com.devpoint.taskflow.authservice.dto.RegistrationRequest;

public interface AuthService {

    AuthResponse authenticateUser(LoginRequest loginRequest);

    AuthResponse registerUser(RegistrationRequest user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
