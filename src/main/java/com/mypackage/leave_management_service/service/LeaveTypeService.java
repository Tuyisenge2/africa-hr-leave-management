package com.mypackage.leave_management_service.service;

import com.mypackage.leave_management_service.dto.LeaveTypeDTO;
import com.mypackage.leave_management_service.model.LeaveType;
import com.mypackage.leave_management_service.repository.LeaveTypeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveTypeService {
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeService(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public LeaveTypeDTO createLeaveType(LeaveTypeDTO leaveTypeDTO) {
        LeaveType leaveType = new LeaveType();
        BeanUtils.copyProperties(leaveTypeDTO, leaveType);
        LeaveType savedLeaveType = leaveTypeRepository.save(leaveType);
        BeanUtils.copyProperties(savedLeaveType, leaveTypeDTO);
        return leaveTypeDTO;
    }

    public List<LeaveTypeDTO> getAllLeaveTypes() {
        return leaveTypeRepository.findAll().stream()
                .map(leaveType -> {
                    LeaveTypeDTO dto = new LeaveTypeDTO();
                    BeanUtils.copyProperties(leaveType, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public LeaveTypeDTO updateLeaveType(Integer id, LeaveTypeDTO leaveTypeDTO) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));
        
        // Update only the fields that should be updated
        leaveType.setName(leaveTypeDTO.getName());
        leaveType.setDeductible(leaveTypeDTO.isDeductible());
        leaveType.setRequiresDocument(leaveTypeDTO.isRequiresDocument());
        
        LeaveType updatedLeaveType = leaveTypeRepository.save(leaveType);
        BeanUtils.copyProperties(updatedLeaveType, leaveTypeDTO);
        return leaveTypeDTO;
    }

    public void deleteLeaveType(Integer id) {
        leaveTypeRepository.deleteById(id);
    }
} 