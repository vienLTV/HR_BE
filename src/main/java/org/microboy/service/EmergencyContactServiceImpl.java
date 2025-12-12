package org.microboy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microboy.constants.ExceptionConstants;
import org.microboy.dto.EmergencyContactDTO;
import org.microboy.entity.EmergencyContactEntity;
import org.microboy.entity.EmployeeCoreEntity;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class EmergencyContactServiceImpl implements EmergencyContactService {

    private final ObjectMapper objectMapper;

    /**
     * Creates a new emergency contact for the specified employee.
     *
     * @param emergencyContactDTO the emergency contact data, must not be null.
     * @return the created {@link EmergencyContactDTO} with the generated contact ID.
     * @throws BadRequestException if the data is null or the employee does not exist.
     */
    @Override
    public EmergencyContactDTO createEmergencyContact(EmergencyContactDTO emergencyContactDTO) {

        // Verify request
        if (emergencyContactDTO == null) {
            throw new BadRequestException("Requested emergency contact is null");
        }

        // Verify employee
        EmployeeCoreEntity employeeCoreEntity = EmployeeCoreEntity.findById(emergencyContactDTO.getEmployeeId());
        if (employeeCoreEntity == null) {
            throw new BadRequestException(ExceptionConstants.EMPLOYEE_NOT_FOUND);
        }

        EmergencyContactEntity emergencyContactEntity = objectMapper.convertValue(emergencyContactDTO, EmergencyContactEntity.class);
        EmergencyContactEntity.persist(emergencyContactEntity);
        emergencyContactDTO.setEmergencyContactId(emergencyContactEntity.employeeId);

        log.info("Created emergency contact with id {}", emergencyContactDTO.getEmployeeId());
        return emergencyContactDTO;
    }

    /**
     * Updates an existing emergency contact with the given ID.
     *
     * @param emergencyContactDTO the emergency contact data to update, must not be null.
     * @param id the UUID of the emergency contact to be updated, must not be null.
     * @return the updated {@link EmergencyContactDTO}.
     * @throws BadRequestException if the contact data or ID is null, the emergency contact does not exist,
     *                             or the employee does not exist.
     */
    @Override
    public EmergencyContactDTO updateEmergencyContact(EmergencyContactDTO emergencyContactDTO, UUID id) {

        if (emergencyContactDTO == null || id == null) {
            throw new BadRequestException("Requested emergency contact is null or id not given");
        }

        EmergencyContactEntity emergencyContactEntity = EmergencyContactEntity.findById(id);
        if (emergencyContactEntity == null) {
            throw new BadRequestException(ExceptionConstants.EMERGENCY_CONTACT_NOT_FOUND);
        }

        EmployeeCoreEntity employeeCoreEntity = EmployeeCoreEntity.findById(emergencyContactDTO.getEmployeeId());
        if (employeeCoreEntity == null) {
            throw new BadRequestException(ExceptionConstants.EMPLOYEE_NOT_FOUND);
        }

        objectMapper.convertValue(emergencyContactDTO, EmergencyContactEntity.class);

        log.info("Successfully update emergency contact with given id: {}", id);
        return emergencyContactDTO;
    }

    /**
     * Retrieves a list of all emergency contacts associated with the specified employee ID.
     *
     * @param employeeId the UUID of the employee whose emergency contacts are to be retrieved, must not be null.
     * @return a list of {@link EmergencyContactDTO} objects representing the emergency contacts for the specified employee.
     */
    @Override
    public List<EmergencyContactDTO> findAllEmergencyContactByEmployeeId(UUID employeeId) {
        return EmergencyContactEntity.findByEmployeeId(employeeId)
                .stream()
                .map(emergencyContactEntity -> objectMapper.convertValue(emergencyContactEntity, EmergencyContactDTO.class)).toList();
    }

    /**
     * Retrieves an emergency contact by its ID.
     *
     * @param id the UUID of the emergency contact to retrieve, must not be null.
     * @return the {@link EmergencyContactDTO} representing the found emergency contact.
     * @throws BadRequestException if the provided ID is null.
     * @throws EntityNotFoundException if no emergency contact is found with the given ID.
     */
    @Override
    public EmergencyContactDTO findEmergencyContactById(UUID id) {
        if (id == null) {
            throw new BadRequestException(ExceptionConstants.ID_NOT_GIVEN);
        }

        EmergencyContactEntity emergencyContactEntity = EmergencyContactEntity.findById(id);
        if (emergencyContactEntity == null) {
            log.error("Could not find emergency contact with given id : {}", id);
            throw new EntityNotFoundException(ExceptionConstants.ENTITY_NOT_FOUND);
        }

        return objectMapper.convertValue(emergencyContactEntity, EmergencyContactDTO.class);
    }

    /**
     * Deletes an emergency contact by its ID.
     *
     * @param id the UUID of the emergency contact to delete, must not be null.
     * @throws BadRequestException if the provided ID is null.
     * @throws EntityNotFoundException if no emergency contact is found with the given ID.
     */
    @Override
    public void deleteEmergencyContactById(UUID id) {
        if (id == null) {
            throw new BadRequestException(ExceptionConstants.ID_NOT_GIVEN);
        }

        EmergencyContactEntity emergencyContactEntity = EmergencyContactEntity.findById(id);
        if (emergencyContactEntity == null) {
            log.error("Could not find emergency contact with given id : {}", id);
            throw new EntityNotFoundException(ExceptionConstants.ENTITY_NOT_FOUND);
        }

        EmergencyContactEntity.deleteById(id);
        log.info("Deleted emergency contact with id: {}", id);
    }
}
