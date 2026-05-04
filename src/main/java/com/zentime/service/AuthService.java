package com.zentime.service;

import com.zentime.dto.AuthResponse;
import com.zentime.dto.LoginRequest;
import com.zentime.dto.RegisterRequest;
import com.zentime.model.User;
import com.zentime.repository.UserRepository;
import com.zentime.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Handles user registration and authentication, returning a signed JWT on success.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Creates a new user account and issues a JWT.
     *
     * @param request email, raw password and role
     * @return a JWT for the newly created user
     * @throws ResponseStatusException 409 if the email is already registered
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user));
    }

    /**
     * Authenticates credentials via Spring Security and issues a JWT.
     * <p>
     * The principal is cast to {@link User} because our {@code UserDetailsService}
     * always loads the concrete entity — never a plain {@code UserDetails}.
     *
     * @param request email and raw password
     * @return a JWT for the authenticated user
     * @throws ResponseStatusException 401 if credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        return new AuthResponse(jwtService.generateToken(user));
    }
}