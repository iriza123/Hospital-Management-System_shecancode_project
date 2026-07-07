package org.example.com.hospitalmanagementsystem.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Async
    public void sendAppointmentBookedEmail(String toEmail, String patientName, String appointmentDate, String appointmentTime) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Appointment Booked Successfully");
            message.setText(
                    "Dear " + patientName + ",\n\n" +
                    "Your appointment has been booked successfully.\n\n" +
                    "Date: " + appointmentDate + "\n" +
                    "Time: " + appointmentTime + "\n\n" +
                    "Status: PENDING (Awaiting approval)\n\n" +
                    "You will receive another email once the appointment is approved.\n\n" +
                    "Best regards,\n" +
                    "Hospital Management System"
            );
            mailSender.send(message);
            log.info("Appointment booked email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send appointment booked email to: {}", toEmail, e);
        }
    }

    @Async
    public void sendAppointmentApprovedEmail(String toEmail, String patientName, String doctorName, String appointmentDate, String appointmentTime) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Appointment Approved");
            message.setText(
                    "Dear " + patientName + ",\n\n" +
                    "Your appointment has been APPROVED.\n\n" +
                    "Doctor: " + doctorName + "\n" +
                    "Date: " + appointmentDate + "\n" +
                    "Time: " + appointmentTime + "\n\n" +
                    "Please arrive 15 minutes early for check-in.\n\n" +
                    "Best regards,\n" +
                    "Hospital Management System"
            );
            mailSender.send(message);
            log.info("Appointment approved email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send appointment approved email to: {}", toEmail, e);
        }
    }

    @Async
    public void sendAppointmentRejectedEmail(String toEmail, String patientName, String appointmentDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Appointment Rejected");
            message.setText(
                    "Dear " + patientName + ",\n\n" +
                    "Unfortunately, your appointment request for " + appointmentDate + " has been rejected.\n\n" +
                    "Please contact the hospital to schedule a new appointment or choose a different date.\n\n" +
                    "Best regards,\n" +
                    "Hospital Management System"
            );
            mailSender.send(message);
            log.info("Appointment rejected email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send appointment rejected email to: {}", toEmail, e);
        }
    }

    @Async
    public void sendDoctorCreatedEmail(String toEmail, String doctorName, String email, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome - Doctor Account Created");
            message.setText(
                    "Dear Dr. " + doctorName + ",\n\n" +
                    "Your doctor account has been created successfully.\n\n" +
                    "Login Credentials:\n" +
                    "Email: " + email + "\n" +
                    "Password: " + password + "\n\n" +
                    "Please login and change your password immediately.\n\n" +
                    "Best regards,\n" +
                    "Hospital Management System"
            );
            mailSender.send(message);
            log.info("Doctor created email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send doctor created email to: {}", toEmail, e);
        }
    }

    @Async
    public void sendDiagnosisUploadedEmail(String toEmail, String patientName, String doctorName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Diagnosis and Prescription Available");
            message.setText(
                    "Dear " + patientName + ",\n\n" +
                    "Dr. " + doctorName + " has uploaded your diagnosis and prescription.\n\n" +
                    "Please login to view your diagnosis details and download your prescription.\n\n" +
                    "Best regards,\n" +
                    "Hospital Management System"
            );
            mailSender.send(message);
            log.info("Diagnosis uploaded email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send diagnosis uploaded email to: {}", toEmail, e);
        }
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to Hospital Management System");
            message.setText(
                    "Dear " + fullName + ",\n\n" +
                    "Welcome to our Hospital Management System!\n\n" +
                    "Your account has been created successfully.\n" +
                    "You can now book appointments and manage your medical records.\n\n" +
                    "Best regards,\n" +
                    "Hospital Management System"
            );
            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }
}
