package com.mypackage.leave_management_service.service;

import com.mypackage.leave_management_service.dto.LeaveApplicationDTO;
import com.mypackage.leave_management_service.model.LeaveApplication;
import com.mypackage.leave_management_service.model.LeaveType;
import com.mypackage.leave_management_service.model.LeaveStatus;
import com.mypackage.leave_management_service.repository.LeaveApplicationRepository;
import com.mypackage.leave_management_service.repository.LeaveTypeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveApplicationService {
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveApplicationService(LeaveApplicationRepository leaveApplicationRepository,
                                 LeaveTypeRepository leaveTypeRepository) {
        this.leaveApplicationRepository = leaveApplicationRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public LeaveApplicationDTO createLeaveApplication(LeaveApplicationDTO leaveApplicationDTO) {
        try {
            LeaveApplication leaveApplication = new LeaveApplication();
            BeanUtils.copyProperties(leaveApplicationDTO, leaveApplication);
            
            // Set default status if not provided
            if (leaveApplicationDTO.getStatus() == null) {
                leaveApplicationDTO.setStatus(LeaveStatus.PENDING);
            }
            leaveApplication.setStatus(leaveApplicationDTO.getStatus());
            
            // Validate leave type exists
            LeaveType leaveType = leaveTypeRepository.findById(leaveApplicationDTO.getLeaveTypeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "Leave type with ID " + leaveApplicationDTO.getLeaveTypeId() + " not found. Please create a leave type first."));
            leaveApplication.setLeaveType(leaveType);
            
            LeaveApplication savedApplication = leaveApplicationRepository.save(leaveApplication);
            BeanUtils.copyProperties(savedApplication, leaveApplicationDTO);
            return leaveApplicationDTO;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error creating leave application: " + e.getMessage());
        }
    }

    public List<LeaveApplicationDTO> getAllLeaveApplications() {
        return leaveApplicationRepository.findAll().stream()
                .map(application -> {
                    LeaveApplicationDTO dto = new LeaveApplicationDTO();
                    BeanUtils.copyProperties(application, dto);
                    dto.setLeaveTypeId(application.getLeaveType().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<LeaveApplicationDTO> getEmployeeLeaveApplications(Integer employeeId) {
        return leaveApplicationRepository.findByEmployeeId(employeeId).stream()
                .map(application -> {
                    LeaveApplicationDTO dto = new LeaveApplicationDTO();
                    BeanUtils.copyProperties(application, dto);
                    dto.setLeaveTypeId(application.getLeaveType().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public LeaveApplicationDTO updateLeaveApplication(Integer id, LeaveApplicationDTO leaveApplicationDTO) {
        try {
            LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Leave application with ID " + id + " not found"));
            
            // Update only the fields that should be updated
            if (leaveApplicationDTO.getEmployeeId() != null) {
                leaveApplication.setEmployeeId(leaveApplicationDTO.getEmployeeId());
            }
            if (leaveApplicationDTO.getStartDate() != null) {
                leaveApplication.setStartDate(leaveApplicationDTO.getStartDate());
            }
            if (leaveApplicationDTO.getEndDate() != null) {
                leaveApplication.setEndDate(leaveApplicationDTO.getEndDate());
            }
            if (leaveApplicationDTO.getReason() != null) {
                leaveApplication.setReason(leaveApplicationDTO.getReason());
            }
            if (leaveApplicationDTO.getDocumentUrl() != null) {
                leaveApplication.setDocumentUrl(leaveApplicationDTO.getDocumentUrl());
            }
            if (leaveApplicationDTO.getStatus() != null) {
                leaveApplication.setStatus(leaveApplicationDTO.getStatus());
            }
            
            if (leaveApplicationDTO.getLeaveTypeId() != null) {
                LeaveType leaveType = leaveTypeRepository.findById(leaveApplicationDTO.getLeaveTypeId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                            "Leave type with ID " + leaveApplicationDTO.getLeaveTypeId() + " not found"));
                leaveApplication.setLeaveType(leaveType);
            }
            
            LeaveApplication updatedApplication = leaveApplicationRepository.save(leaveApplication);
            LeaveApplicationDTO responseDTO = new LeaveApplicationDTO();
            BeanUtils.copyProperties(updatedApplication, responseDTO);
            responseDTO.setLeaveTypeId(updatedApplication.getLeaveType().getId());
            return responseDTO;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error updating leave application: " + e.getMessage());
        }
    }

    public void deleteLeaveApplication(Integer id) {
        try {
            if (!leaveApplicationRepository.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Leave application with ID " + id + " not found");
            }
            leaveApplicationRepository.deleteById(id);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error deleting leave application: " + e.getMessage());
        }
    }
} 