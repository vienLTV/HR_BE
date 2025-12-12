package org.microboy.service;

import java.util.List;
import java.util.UUID;

import org.microboy.constants.ExceptionConstants;
import org.microboy.dto.request.EmployeeHistoryRequestDTO;
import org.microboy.dto.response.EmployeeHistoryResponseDTO;
import org.microboy.dto.response.PaginatedResponse;
import org.microboy.entity.EmployeeCoreEntity;
import org.microboy.entity.EmployeeHistoryEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

        return objectMapper.convertValue(employeeHistory, EmployeeHistoryResponseDTO.class);
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

        employeeHistory.companyName = employeeHistoryDTO.getCompanyName();
        employeeHistory.companyAddress = employeeHistoryDTO.getCompanyAddress();
        employeeHistory.employmentType = employeeHistoryDTO.getEmploymentType();
        employeeHistory.jobTitle = employeeHistoryDTO.getJobTitle();
        employeeHistory.startDate = employeeHistoryDTO.getStartDate();
        employeeHistory.endDate = employeeHistoryDTO.getEndDate();

        log.info("Updated employee history with id {}", employeeHistory.employeeHistoryId);

        return objectMapper.convertValue(employeeHistoryDTO, EmployeeHistoryResponseDTO.class);
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
    public PaginatedResponse<EmployeeHistoryResponseDTO> findAllEmployeeHistoryByEmployeeId(
        UUID employeeId,
        int page,
        int pageSize
    ) {
        PaginatedResponse<EmployeeHistoryResponseDTO> response = new PaginatedResponse<>();

        List<EmployeeHistoryEntity> employeeHistoryEntities = EmployeeHistoryEntity.getPageByEmployeeId(
            employeeId,
            page,
            pageSize
        );

        long totalItems = EmployeeHistoryEntity.getTotalItems(employeeId);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        List<EmployeeHistoryResponseDTO> employeeHistoryResponses = employeeHistoryEntities
            .stream()
            .map(employeeHistory -> objectMapper.convertValue(employeeHistory, EmployeeHistoryResponseDTO.class))
            .toList();

        response.setItems(employeeHistoryResponses);
        response.setTotalItems(totalItems);
        response.setTotalPages(totalPages);
        response.setCurrentPage(page);
        response.setPageSize(pageSize);

        return response;
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

        return objectMapper.convertValue(employeeHistory, EmployeeHistoryResponseDTO.class);
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
