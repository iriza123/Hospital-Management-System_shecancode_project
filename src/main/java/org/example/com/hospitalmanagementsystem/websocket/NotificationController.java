package org.example.com.hospitalmanagementsystem.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    @MessageMapping("/ping")
    @SendToUser("/queue/pong")
    public NotificationPayload handlePing(Principal principal) {
        return NotificationPayload.builder()
                .type("PONG")
                .title("Connection Active")
                .message("WebSocket connection is live.")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @MessageMapping("/subscribe/appointments")
    @SendTo("/topic/appointments")
    public NotificationPayload handleSubscribeAppointments(Principal principal) {
        return NotificationPayload.builder()
                .type("SUBSCRIBED")
                .title("Subscribed")
                .message("You are now subscribed to appointment updates.")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
