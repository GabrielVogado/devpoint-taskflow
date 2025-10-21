package br.com.devpoint.taskflow.authservice.service.impl;

import br.com.devpoint.taskflow.authservice.dto.AuthResponse;
import br.com.devpoint.taskflow.authservice.dto.LoginRequest;
import br.com.devpoint.taskflow.authservice.dto.RegistrationRequest;
import br.com.devpoint.taskflow.authservice.enums.Role;
import br.com.devpoint.taskflow.authservice.model.User;
import br.com.devpoint.taskflow.authservice.repository.UserRepository;
import br.com.devpoint.taskflow.authservice.service.AuthService;
import br.com.devpoint.taskflow.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;


    /**
     * @param loginRequest
     * @return
     */
    @Override
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        System.out.println("Tentando autenticar: " + loginRequest.getUsername());
        System.out.println("Senha fornecida: " + loginRequest.getPassword());

        // Verifique se o usuário existe e qual a senha armazenada
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("Senha armazenada: " + user.getPassword());
        System.out.println("Senha matches: " + passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User authenticatedUser = (User) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(authenticatedUser);

        return new AuthResponse(
                jwt,
                "Bearer",
                authenticatedUser.getId(),
                authenticatedUser.getUsername(),
                authenticatedUser.getEmail(),
                authenticatedUser.getRole().name()
        );
    }

    /**
     * @param registrationRequest
     * @return
     */
    @Override
    public AuthResponse registerUser(RegistrationRequest registrationRequest) {
        if (existsByUsername(registrationRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        if (existsByEmail(registrationRequest.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }
        User user = User.builder()
                .username(registrationRequest.getUsername())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .role(Role.USER)
                .enabled(true)
                .build();
        User registeredUser = userRepository.save(user);

        // Gera o token diretamente sem tentar autenticar novamente
        String jwt = jwtUtil.generateToken(registeredUser);

        return new AuthResponse(
                jwt,
                "Bearer",
                registeredUser.getId(),
                registeredUser.getUsername(),
                registeredUser.getEmail(),
                registeredUser.getRole().name()
        );
    }

    /**
     * @param username
     * @return
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * @param email
     * @return
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
