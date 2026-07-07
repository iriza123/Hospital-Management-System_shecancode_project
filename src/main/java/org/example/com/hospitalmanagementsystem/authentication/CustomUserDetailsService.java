package org.example.com.hospitalmanagementsystem.authentication;

import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.repository.AdminRepository;
import org.example.com.hospitalmanagementsystem.repository.DoctorRepository;
import org.example.com.hospitalmanagementsystem.repository.PatientRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            var a = admin.get();
            return new CustomUserDetails(a.getId(), a.getEmail(), a.getPassword(), a.getRole());
        }

        var doctor = doctorRepository.findByEmail(email);
        if (doctor.isPresent()) {
            var d = doctor.get();
            return new CustomUserDetails(d.getId(), d.getEmail(), d.getPassword(), d.getRole());
        }

        var patient = patientRepository.findByEmail(email);
        if (patient.isPresent()) {
            var p = patient.get();
            return new CustomUserDetails(p.getId(), p.getEmail(), p.getPassword(), p.getRole());
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
