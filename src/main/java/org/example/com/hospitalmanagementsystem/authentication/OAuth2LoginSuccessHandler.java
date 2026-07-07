package org.example.com.hospitalmanagementsystem.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.entity.Patient;
import org.example.com.hospitalmanagementsystem.enums.Gender;
import org.example.com.hospitalmanagementsystem.repository.PatientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final PatientRepository patientRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Patient patient = patientRepository.findByEmail(email).orElseGet(() -> {
            Patient newPatient = Patient.builder()
                    .fullName(name != null ? name : "OAuth User")
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .dateOfBirth(LocalDate.of(2000, 1, 1))
                    .gender(Gender.OTHER)
                    .phoneNumber("+000000000000")
                    .build();
            return patientRepository.save(newPatient);
        });

        CustomUserDetails userDetails = new CustomUserDetails(
                patient.getId(),
                patient.getEmail(),
                patient.getPassword(),
                patient.getRole()
        );

        String token = jwtService.generateToken(userDetails);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"token\":\"" + token + "\"," +
                "\"tokenType\":\"Bearer\"," +
                "\"email\":\"" + email + "\"," +
                "\"role\":\"PATIENT\"}"
        );
    }
}
