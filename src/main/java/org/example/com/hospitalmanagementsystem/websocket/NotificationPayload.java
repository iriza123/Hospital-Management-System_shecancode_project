package org.example.com.hospitalmanagementsystem.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPayload {

    private String type;
    private String title;
    private String message;
    private Long referenceId;
    private LocalDateTime timestamp;
}
