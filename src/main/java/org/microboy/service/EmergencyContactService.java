package org.microboy.service;

import java.util.List;
import java.util.UUID;

import org.microboy.dto.EmergencyContactDTO;

public interface EmergencyContactService {

    EmergencyContactDTO createEmergencyContact(EmergencyContactDTO emergencyContactDTO);

    EmergencyContactDTO updateEmergencyContact(EmergencyContactDTO emergencyContactDTO, UUID id);

    List<EmergencyContactDTO> findAllEmergencyContactByEmployeeId(UUID employeeId);

    EmergencyContactDTO findEmergencyContactById(UUID id);

    void deleteEmergencyContactById(UUID id);

}
