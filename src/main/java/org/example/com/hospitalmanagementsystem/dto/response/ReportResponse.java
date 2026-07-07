package org.example.com.hospitalmanagementsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ReportResponse implements Serializable {
    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long pendingAppointments;
    private long approvedAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private long rejectedAppointments;
    private long dailyAppointments;
    private long weeklyAppointments;
    private long monthlyAppointments;
    private List<Map<String, Object>> appointmentsPerService;
    private List<Map<String, Object>> doctorWorkload;
    private String mostRequestedService;
}
