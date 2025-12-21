package org.microboy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microboy.constants.ExceptionConstants;
import org.microboy.dto.request.EmployeeHistoryRequestDTO;
import org.microboy.dto.response.EmployeeHistoryResponseDTO;
import org.microboy.entity.EmployeeCoreEntity;
import org.microboy.entity.EmployeeHistoryEntity;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class EmployeeHistoryServiceImpl implements EmployeeHistoryService {

    private final ObjectMapper objectMapper;

    /**
     * Creates a new employee history record.
     *
     * @param employeeHistoryDTO the employee history data transfer object to create, must not be null.
     * @return the {@link EmployeeHistoryResponseDTO} containing the created employee history and job title information.
     * @throws BadRequestException if the provided employee history data is null, the associated employee does not exist,
     *                             or the specified job title does not exist.
     */
    @Override
    @Transactional
    public EmployeeHistoryResponseDTO createEmployeeHistory(EmployeeHistoryRequestDTO employeeHistoryDTO) {
        // Verify request
        if (employeeHistoryDTO == null) {
            throw new BadRequestException("Requested employee history is null");
        }

        // Verify employee
        EmployeeCoreEntity employeeCoreEntity = EmployeeCoreEntity.findById(employeeHistoryDTO.getEmployeeId());
        if (employeeCoreEntity == null) {
            throw new BadRequestException(ExceptionConstants.EMPLOYEE_NOT_FOUND);
        }

        EmployeeHistoryEntity employeeHistory = objectMapper.convertValue(
            employeeHistoryDTO,
            EmployeeHistoryEntity.class
        );
        EmployeeHistoryEntity.persist(employeeHistory);
        log.info("Created employee history with id {}", employeeHistory.employeeHistoryId);

        EmployeeHistoryResponseDTO response = objectMapper.convertValue(employeeHistory, EmployeeHistoryResponseDTO.class);
        response.setId(employeeHistory.employeeHistoryId);
        return response;
    }

    /**
     * Updates an existing employee history record by its ID.
     *
     * @param employeeHistoryDTO the employee history data transfer object containing the updated data, must not be null.
     * @param id                 the UUID of the employee history record to update, must not be null.
     * @return the {@link EmployeeHistoryResponseDTO} containing the updated employee history and job title information.
     * @throws BadRequestException if the provided employee history data or ID is null, the employee history does not exist,
     *                             the associated employee does not exist, or the specified job title does not exist.
     */
    @Override
    @Transactional
    public EmployeeHistoryResponseDTO updateEmployeeHistory(EmployeeHistoryRequestDTO employeeHistoryDTO, UUID id) {
        // Verify request
        if (employeeHistoryDTO == null || id == null) {
            throw new BadRequestException("Requested employee history is null");
        }

        // Verify employee history
        EmployeeHistoryEntity employeeHistory = EmployeeHistoryEntity.findById(id);
        if (employeeHistory == null) {
            throw new BadRequestException(ExceptionConstants.EMPLOYEE_HISTORY_NOT_FOUND);
        }

        // Verify employee
        EmployeeCoreEntity employeeCoreEntity = EmployeeCoreEntity.findById(employeeHistoryDTO.getEmployeeId());
        if (employeeCoreEntity == null) {
            throw new BadRequestException(ExceptionConstants.EMPLOYEE_NOT_FOUND);
        }

        employeeHistory.fieldName = employeeHistoryDTO.getFieldName();
        employeeHistory.oldValue = employeeHistoryDTO.getOldValue();
        employeeHistory.newValue = employeeHistoryDTO.getNewValue();
        employeeHistory.changeType = employeeHistoryDTO.getChangeType();
        employeeHistory.changedBy = employeeHistoryDTO.getChangedBy();
        employeeHistory.changedAt = employeeHistoryDTO.getChangedAt();

        log.info("Updated employee history with id {}", employeeHistory.employeeHistoryId);

        EmployeeHistoryResponseDTO response = objectMapper.convertValue(employeeHistory, EmployeeHistoryResponseDTO.class);
        response.setId(employeeHistory.employeeHistoryId);
        return response;
    }

    /**
     * Retrieves a paginated list of employee history records for a specified employee.
     *
     * @param employeeId the UUID of the employee whose history records are to be retrieved, must not be null.
     * @param page       the page number to retrieve, must be greater than or equal to 0.
     * @param pageSize   the number of records to return per page, must be greater than 0.
     * @return a {@link PaginatedResponse} containing a list of {@link EmployeeHistoryResponseDTO} objects,
     * total number of items, total number of pages, current page, and page size.
     */
    @Override
    public List<EmployeeHistoryResponseDTO> findAllEmployeeHistoryByEmployeeId(
        UUID employeeId
    ) {
        List<EmployeeHistoryEntity> employeeHistoryEntities = EmployeeHistoryEntity.getPageByEmployeeId(
            employeeId,
            0,
            Integer.MAX_VALUE
        );

        return employeeHistoryEntities
            .stream()
            .map(employeeHistory -> {
                EmployeeHistoryResponseDTO dto = objectMapper.convertValue(employeeHistory, EmployeeHistoryResponseDTO.class);
                dto.setId(employeeHistory.employeeHistoryId);
                return dto;
            })
            .toList();
    }

    /**
     * Retrieves an employee history record by its ID.
     *
     * @param id the UUID of the employee history record to retrieve, must not be null.
     * @return the {@link EmployeeHistoryResponseDTO} representing the found employee history record.
     * @throws BadRequestException     if the provided ID is null.
     * @throws EntityNotFoundException if no employee history record is found with the given ID.
     */
    @Override
    public EmployeeHistoryResponseDTO findEmployeeHistoryById(UUID id) {
        if (id == null) {
            throw new BadRequestException(ExceptionConstants.ID_NOT_GIVEN);
        }

        EmployeeHistoryEntity employeeHistory = EmployeeHistoryEntity.findById(id);
        if (employeeHistory == null) {
            throw new EntityNotFoundException(ExceptionConstants.EMPLOYEE_HISTORY_NOT_FOUND);
        }

        EmployeeHistoryResponseDTO response = objectMapper.convertValue(employeeHistory, EmployeeHistoryResponseDTO.class);
        response.setId(employeeHistory.employeeHistoryId);
        return response;
    }

    /**
     * Deletes an employee history record by its ID.
     *
     * @param id the UUID of the employee history record to delete, must not be null.
     * @throws BadRequestException     if the provided ID is null.
     * @throws EntityNotFoundException if no employee history record is found with the given ID.
     */
    @Override
    public void deleteEmployeeHistoryById(UUID id) {
        if (id == null) {
            throw new BadRequestException(ExceptionConstants.ID_NOT_GIVEN);
        }

        EmployeeHistoryEntity employeeHistory = EmployeeHistoryEntity.findById(id);
        if (employeeHistory == null) {
            throw new EntityNotFoundException(ExceptionConstants.EMPLOYEE_HISTORY_NOT_FOUND);
        }
        EmployeeHistoryEntity.deleteById(id);

        log.info("Deleted employee history with id: {}", id);
    }
}
