package org.example.com.hospitalmanagementsystem.authentication;

import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.LoginRequest;
import org.example.com.hospitalmanagementsystem.dto.response.AuthResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .email(userDetails.getUsername())
                .role(userDetails.getRole().name())
                .build();
    }
}
