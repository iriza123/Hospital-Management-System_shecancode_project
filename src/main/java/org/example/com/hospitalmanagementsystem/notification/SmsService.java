package org.example.com.hospitalmanagementsystem.notification;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    @Async
    public void sendAppointmentReminder(String toPhone, String patientName, String doctorName, String date, String time) {
        try {
            String body = "Hello " + patientName + ", reminder: appointment with Dr. " + doctorName
                    + " on " + date + " at " + time + ". Hospital Management System.";
            Message.creator(new PhoneNumber(toPhone), new PhoneNumber(fromNumber), body).create();
            log.info("SMS reminder sent to {}", toPhone);
        } catch (Exception e) {
            log.error("Failed to send SMS reminder to {}: {}", toPhone, e.getMessage());
        }
    }

    @Async
    public void sendAppointmentConfirmation(String toPhone, String patientName, String date, String time) {
        try {
            String body = "Dear " + patientName + ", your appointment on " + date + " at " + time
                    + " is confirmed. Hospital Management System.";
            Message.creator(new PhoneNumber(toPhone), new PhoneNumber(fromNumber), body).create();
            log.info("SMS confirmation sent to {}", toPhone);
        } catch (Exception e) {
            log.error("Failed to send SMS confirmation to {}: {}", toPhone, e.getMessage());
        }
    }

    @Async
    public void sendAppointmentCancellationNotice(String toPhone, String patientName, String date) {
        try {
            String body = "Dear " + patientName + ", your appointment on " + date
                    + " has been cancelled. Contact us to reschedule. Hospital Management System.";
            Message.creator(new PhoneNumber(toPhone), new PhoneNumber(fromNumber), body).create();
            log.info("SMS cancellation notice sent to {}", toPhone);
        } catch (Exception e) {
            log.error("Failed to send SMS cancellation to {}: {}", toPhone, e.getMessage());
        }
    }
}
