package com.mypackage.leave_management_service.dto;

import com.mypackage.leave_management_service.model.LeaveStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveApplicationDTO {
    private Integer id;
    private Integer employeeId;
    private Integer leaveTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String documentUrl;
    private LeaveStatus status;
} 