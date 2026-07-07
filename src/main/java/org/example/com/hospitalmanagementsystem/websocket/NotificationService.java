package org.example.com.hospitalmanagementsystem.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyAppointmentBooked(Long patientId, Long appointmentId, String doctorName, String date) {
        NotificationPayload payload = NotificationPayload.builder()
                .type("APPOINTMENT_BOOKED")
                .title("Appointment Booked")
                .message("Your appointment with Dr. " + doctorName + " on " + date + " is pending approval.")
                .referenceId(appointmentId)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSendToUser(
                patientId.toString(),
                "/queue/notifications",
                payload
        );

        messagingTemplate.convertAndSend("/topic/appointments", payload);
        log.info("WebSocket appointment booked notification sent to patient {}", patientId);
    }

    public void notifyAppointmentApproved(Long patientId, Long appointmentId, String doctorName, String date) {
        NotificationPayload payload = NotificationPayload.builder()
                .type("APPOINTMENT_APPROVED")
                .title("Appointment Approved")
                .message("Your appointment with Dr. " + doctorName + " on " + date + " has been approved.")
                .referenceId(appointmentId)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSendToUser(
                patientId.toString(),
                "/queue/notifications",
                payload
        );

        messagingTemplate.convertAndSend("/topic/appointments", payload);
        log.info("WebSocket appointment approved notification sent to patient {}", patientId);
    }

    public void notifyAppointmentRejected(Long patientId, Long appointmentId, String date) {
        NotificationPayload payload = NotificationPayload.builder()
                .type("APPOINTMENT_REJECTED")
                .title("Appointment Rejected")
                .message("Your appointment request for " + date + " has been rejected.")
                .referenceId(appointmentId)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSendToUser(
                patientId.toString(),
                "/queue/notifications",
                payload
        );
        log.info("WebSocket appointment rejected notification sent to patient {}", patientId);
    }

    public void notifyAppointmentCancelled(Long patientId, Long appointmentId, String date) {
        NotificationPayload payload = NotificationPayload.builder()
                .type("APPOINTMENT_CANCELLED")
                .title("Appointment Cancelled")
                .message("Your appointment on " + date + " has been cancelled.")
                .referenceId(appointmentId)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSendToUser(
                patientId.toString(),
                "/queue/notifications",
                payload
        );
        log.info("WebSocket appointment cancelled notification sent to patient {}", patientId);
    }

    public void notifyDiagnosisUploaded(Long patientId, Long appointmentId, String doctorName) {
        NotificationPayload payload = NotificationPayload.builder()
                .type("DIAGNOSIS_UPLOADED")
                .title("Diagnosis Available")
                .message("Dr. " + doctorName + " has uploaded your diagnosis and prescription.")
                .referenceId(appointmentId)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSendToUser(
                patientId.toString(),
                "/queue/notifications",
                payload
        );
        log.info("WebSocket diagnosis uploaded notification sent to patient {}", patientId);
    }

    public void broadcastDoctorStatusChanged(Long doctorId, String doctorName, boolean active) {
        NotificationPayload payload = NotificationPayload.builder()
                .type("DOCTOR_STATUS_CHANGED")
                .title("Doctor Availability Updated")
                .message("Dr. " + doctorName + " is now " + (active ? "available" : "unavailable") + ".")
                .referenceId(doctorId)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/doctors", payload);
        log.info("WebSocket doctor status changed broadcast for doctor {}", doctorId);
    }
}
