package org.microboy.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.microboy.constants.ExceptionConstants;
import org.microboy.dto.DepartmentDTO;
import org.microboy.dto.EmployeeOverviewDTO;
import org.microboy.dto.JobTitleDTO;
import org.microboy.dto.TeamDTO;
import org.microboy.dto.request.EmployeeCoreRequestDTO;
import org.microboy.dto.response.EmployeeCoreResponseDTO;
import org.microboy.dto.response.PaginatedResponse;
import org.microboy.entity.DepartmentEntity;
import org.microboy.entity.EmployeeCoreEntity;
import org.microboy.entity.EmployeeHistoryEntity;
import org.microboy.entity.EmployeeJobTitleEntity;
import org.microboy.entity.JobTitleEntity;
import org.microboy.entity.OrganizationEntity;
import org.microboy.entity.TeamEntity;
import org.microboy.entity.TeamMemberEntity;
import org.microboy.enums.EmployeeStatus;
import org.microboy.security.config.OrganizationContext;
import org.microboy.security.entity.UserEntity;
import org.microboy.security.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Khanh Tran
 */
@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final JobTitleService jobTitleService;
    private final ObjectMapper objectMapper;
    private final OrganizationContext organizationContext;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createEmployee(EmployeeCoreRequestDTO employeeRequest) {
        // Verify request
        if (employeeRequest == null) {
            throw new BadRequestException("requested employee is null");
        }

        UUID currentOrganizationId = organizationContext.getCurrentOrganizationId();
        if (currentOrganizationId != null) {
            employeeRequest.setOrganizationId(currentOrganizationId);
        }

        EmployeeCoreEntity employeeEntity = new EmployeeCoreEntity();
        employeeEntity.companyPhoneNumber = employeeRequest.getCompanyPhoneNumber();
        employeeEntity.companyEmail = employeeRequest.getCompanyEmail();
        employeeEntity.organizationId = employeeRequest.getOrganizationId();
        employeeEntity.employeeStatus = employeeRequest.getEmployeeStatus() != null
            ? employeeRequest.getEmployeeStatus()
            : EmployeeStatus.PROBATION;
        employeeEntity.firstName = employeeRequest.getFirstName();
        employeeEntity.lastName = employeeRequest.getLastName();
        employeeEntity.currentAddress = employeeRequest.getCurrentAddress();
        employeeEntity.dateOfBirth = employeeRequest.getDateOfBirth();
        employeeEntity.personalPhoneNumber = employeeRequest.getPersonalPhoneNumber();
        employeeEntity.birthPlace = employeeRequest.getBirthPlace();
        employeeEntity.gender = employeeRequest.getGender();

        // Verify job title
        if (employeeRequest.getJobTitleId() != null) {
            JobTitleEntity jobTitle = JobTitleEntity.findById(employeeRequest.getJobTitleId());
            if (jobTitle == null) {
                throw new BadRequestException("job title not found");
            }
            employeeEntity.jobTitleId = jobTitle.jobTitleId;
        }

        // Verify team
        if (employeeRequest.getTeamId() != null) {
            TeamEntity team = TeamEntity.findById(employeeRequest.getTeamId());
            if (team == null) {
                throw new BadRequestException("team not found");
            }
            employeeEntity.teamId = team.teamId;
        }

        EmployeeCoreEntity.persist(employeeEntity);
        log.info("Created employee with id {}", employeeEntity.employeeId);
    }

    @Override
    @Transactional
    public EmployeeCoreRequestDTO updateEmployee(EmployeeCoreRequestDTO employeeRequest, UUID id) {
        if (employeeRequest == null || id == null) {
            throw new BadRequestException("requested employee is null or id not given");
        }

        UUID currentOrganizationId = organizationContext.getCurrentOrganizationId();
        if (currentOrganizationId != null) {
            employeeRequest.setOrganizationId(currentOrganizationId);
        }

        EmployeeCoreEntity employeeCoreEntity = EmployeeCoreEntity.findById(id);
        if (employeeCoreEntity == null) {
            throw new BadRequestException("employee not found");
        }

        // Verify job title
        if (employeeRequest.getJobTitleId() != null) {
            JobTitleEntity jobTitle = JobTitleEntity.findById(employeeRequest.getJobTitleId());
            if (jobTitle == null) {
                throw new BadRequestException("job title not found");
            }
            employeeCoreEntity.jobTitleId = jobTitle.jobTitleId;
        }

        // Verify team
        if (employeeRequest.getTeamId() != null) {
            TeamEntity team = TeamEntity.findById(employeeRequest.getTeamId());
            if (team == null) {
                throw new BadRequestException("team not found");
            }
            employeeCoreEntity.teamId = team.teamId;
        }

        // Save old values for history tracking
        String oldFirstName = employeeCoreEntity.firstName;
        String oldLastName = employeeCoreEntity.lastName;
        String oldCurrentAddress = employeeCoreEntity.currentAddress;
        String oldPersonalPhoneNumber = employeeCoreEntity.personalPhoneNumber;
        String oldBirthPlace = employeeCoreEntity.birthPlace;
        String oldPersonalEmail = employeeCoreEntity.personalEmail;
        EmployeeStatus oldEmployeeStatus = employeeCoreEntity.employeeStatus;
        UUID oldJobTitleId = employeeCoreEntity.jobTitleId;
        UUID oldTeamId = employeeCoreEntity.teamId;

        // Update employee information
        employeeCoreEntity.organizationId = employeeRequest.getOrganizationId();
        employeeCoreEntity.employeeStatus = employeeRequest.getEmployeeStatus();
        employeeCoreEntity.firstName = employeeRequest.getFirstName();
        employeeCoreEntity.lastName = employeeRequest.getLastName();
        employeeCoreEntity.currentAddress = employeeRequest.getCurrentAddress();
        employeeCoreEntity.dateOfBirth = employeeRequest.getDateOfBirth();
        employeeCoreEntity.personalPhoneNumber = employeeRequest.getPersonalPhoneNumber();
        employeeCoreEntity.birthPlace = employeeRequest.getBirthPlace();
        employeeCoreEntity.gender = employeeRequest.getGender();
        employeeCoreEntity.personalEmail = employeeRequest.getPersonalEmail();
        employeeCoreEntity.maritalStatus = employeeRequest.getMaritalStatus();

        // Create history records for changed fields
        String changedBy = "SYSTEM"; // TODO: Get from JWT token
        Instant changedAt = Instant.now();

        log.info("=== EMPLOYEE UPDATE DEBUG ===");
        log.info("Employee ID: {}", id);
        log.info("Old First Name: '{}', New First Name: '{}'", oldFirstName, employeeRequest.getFirstName());
        log.info("Are they equal? {}", StringUtils.equals(oldFirstName, employeeRequest.getFirstName()));

        // Track First Name changes
        if (!StringUtils.equals(oldFirstName, employeeRequest.getFirstName())) {
            log.info("Creating history record for First Name change");
            createHistoryRecord(id, "First Name", oldFirstName, employeeRequest.getFirstName(), changedBy, changedAt);
        } else {
            log.info("First Name has NOT changed - skipping history");
        }

        // Track Last Name changes
        if (!StringUtils.equals(oldLastName, employeeRequest.getLastName())) {
            createHistoryRecord(id, "Last Name", oldLastName, employeeRequest.getLastName(), changedBy, changedAt);
        }

        // Track Current Address changes
        if (!StringUtils.equals(oldCurrentAddress, employeeRequest.getCurrentAddress())) {
            createHistoryRecord(id, "Current Address", oldCurrentAddress, employeeRequest.getCurrentAddress(), changedBy, changedAt);
        }

        // Track Personal Phone changes
        if (!StringUtils.equals(oldPersonalPhoneNumber, employeeRequest.getPersonalPhoneNumber())) {
            createHistoryRecord(id, "Personal Phone", oldPersonalPhoneNumber, employeeRequest.getPersonalPhoneNumber(), changedBy, changedAt);
        }

        // Track Birth Place changes
        if (!StringUtils.equals(oldBirthPlace, employeeRequest.getBirthPlace())) {
            createHistoryRecord(id, "Birth Place", oldBirthPlace, employeeRequest.getBirthPlace(), changedBy, changedAt);
        }

        // Track Personal Email changes
        if (!StringUtils.equals(oldPersonalEmail, employeeRequest.getPersonalEmail())) {
            createHistoryRecord(id, "Personal Email", oldPersonalEmail, employeeRequest.getPersonalEmail(), changedBy, changedAt);
        }

        // Track Employee Status changes
        if (oldEmployeeStatus != employeeRequest.getEmployeeStatus()) {
            createHistoryRecord(id, "Employee Status", 
                oldEmployeeStatus != null ? oldEmployeeStatus.name() : null, 
                employeeRequest.getEmployeeStatus() != null ? employeeRequest.getEmployeeStatus().name() : null, 
                changedBy, changedAt);
        }

        // Track Job Title changes
        UUID newJobTitleId = employeeRequest.getJobTitleId();
        if (!java.util.Objects.equals(oldJobTitleId, newJobTitleId)) {
            String oldJobTitleName = oldJobTitleId != null ? getJobTitleName(oldJobTitleId) : null;
            String newJobTitleName = newJobTitleId != null ? getJobTitleName(newJobTitleId) : null;
            createHistoryRecord(id, "Job Title", oldJobTitleName, newJobTitleName, changedBy, changedAt);
        }

        // Track Team changes
        UUID newTeamId = employeeRequest.getTeamId();
        if (!java.util.Objects.equals(oldTeamId, newTeamId)) {
            String oldTeamName = oldTeamId != null ? getTeamName(oldTeamId) : null;
            String newTeamName = newTeamId != null ? getTeamName(newTeamId) : null;
            createHistoryRecord(id, "Team", oldTeamName, newTeamName, changedBy, changedAt);
        }

        log.info("Updated employee with id {}", id);
        return employeeRequest;
    }

    @Override
    @Transactional
    public EmployeeOverviewDTO updateEmployeeOverview(EmployeeOverviewDTO employee, UUID id) {
        if (employee == null || id == null) {
            throw new BadRequestException("requested employee is null or id not given");
        }

        EmployeeCoreEntity employeeCoreEntity = EmployeeCoreEntity.findById(id);
        if (employeeCoreEntity == null) {
            throw new BadRequestException("employee not found");
        }

        // Save old values for history tracking
        String oldEmployeeCode = employeeCoreEntity.employeeCode;
        String oldCompanyEmail = employeeCoreEntity.companyEmail;
        String oldCompanyPhoneNumber = employeeCoreEntity.companyPhoneNumber;
        EmployeeStatus oldEmployeeStatus = employeeCoreEntity.employeeStatus;
        UUID oldJobTitleId = employeeCoreEntity.jobTitleId;
        UUID oldTeamId = employeeCoreEntity.teamId;

        // Verify job title
        JobTitleDTO jobTitleDTO = employee.getJobTitle();
        if (jobTitleDTO != null && jobTitleDTO.getJobTitleId() != null) {
            JobTitleEntity jobTitle = JobTitleEntity.findById(jobTitleDTO.getJobTitleId());
            if (jobTitle == null) {
                throw new BadRequestException("job title not found");
            }
            employeeCoreEntity.jobTitleId = jobTitle.jobTitleId;
        }

        // Verify team
        TeamDTO teamDTO = employee.getTeam();
        if (teamDTO != null && teamDTO.getTeamId() != null) {
            TeamEntity team = TeamEntity.findById(teamDTO.getTeamId());
            if (team == null) {
                throw new BadRequestException("team not found");
            }
            employeeCoreEntity.teamId = team.teamId;
        }

        // Update employee core information
        employeeCoreEntity.employeeCode = employee.getEmployeeCode();
        employeeCoreEntity.companyEmail = employee.getCompanyEmail();
        employeeCoreEntity.companyPhoneNumber = employee.getCompanyPhoneNumber();
        employeeCoreEntity.employeeStatus = employee.getEmployeeStatus();

        // Create history records for changed fields
        String changedBy = "SYSTEM"; // TODO: Get from JWT token
        Instant changedAt = Instant.now();

        // Track Employee Code changes
        if (!StringUtils.equals(oldEmployeeCode, employee.getEmployeeCode())) {
            createHistoryRecord(id, "Employee Code", oldEmployeeCode, employee.getEmployeeCode(), changedBy, changedAt);
        }

        // Track Company Email changes
        if (!StringUtils.equals(oldCompanyEmail, employee.getCompanyEmail())) {
            createHistoryRecord(id, "Company Email", oldCompanyEmail, employee.getCompanyEmail(), changedBy, changedAt);
        }

        // Track Company Phone changes
        if (!StringUtils.equals(oldCompanyPhoneNumber, employee.getCompanyPhoneNumber())) {
            createHistoryRecord(id, "Company Phone", oldCompanyPhoneNumber, employee.getCompanyPhoneNumber(), changedBy, changedAt);
        }

        // Track Employee Status changes
        if (oldEmployeeStatus != employee.getEmployeeStatus()) {
            createHistoryRecord(id, "Employee Status", 
                oldEmployeeStatus != null ? oldEmployeeStatus.name() : null, 
                employee.getEmployeeStatus() != null ? employee.getEmployeeStatus().name() : null, 
                changedBy, changedAt);
        }

        // Track Job Title changes
        UUID newJobTitleId = jobTitleDTO != null ? jobTitleDTO.getJobTitleId() : null;
        if (!java.util.Objects.equals(oldJobTitleId, newJobTitleId)) {
            String oldJobTitleName = oldJobTitleId != null ? getJobTitleName(oldJobTitleId) : null;
            String newJobTitleName = newJobTitleId != null ? getJobTitleName(newJobTitleId) : null;
            createHistoryRecord(id, "Job Title", oldJobTitleName, newJobTitleName, changedBy, changedAt);
        }

        // Track Team changes
        UUID newTeamId = teamDTO != null ? teamDTO.getTeamId() : null;
        if (!java.util.Objects.equals(oldTeamId, newTeamId)) {
            String oldTeamName = oldTeamId != null ? getTeamName(oldTeamId) : null;
            String newTeamName = newTeamId != null ? getTeamName(newTeamId) : null;
            createHistoryRecord(id, "Team", oldTeamName, newTeamName, changedBy, changedAt);
        }

        log.info("Updated employee overview with id {}", id);
        return employee;
    }

    private void createHistoryRecord(UUID employeeId, String fieldName, String oldValue, String newValue, String changedBy, Instant changedAt) {
        EmployeeHistoryEntity history = EmployeeHistoryEntity.builder()
            .employeeId(employeeId)
            .fieldName(fieldName)
            .oldValue(oldValue)
            .newValue(newValue)
            .changeType("UPDATE")
            .changedBy(changedBy)
            .changedAt(changedAt)
            .build();
        EmployeeHistoryEntity.persist(history);
        log.info("Created history record for employee {} - {} changed from '{}' to '{}'", employeeId, fieldName, oldValue, newValue);
    }

    private String getJobTitleName(UUID jobTitleId) {
        JobTitleEntity jobTitle = JobTitleEntity.findById(jobTitleId);
        return jobTitle != null ? jobTitle.title : null;
    }

    private String getTeamName(UUID teamId) {
        TeamEntity team = TeamEntity.findById(teamId);
        return team != null ? team.name : null;
    }

    /**
     * Retrieves all employees stored in the database.
     *
     * @return A {@link List} of {@link EmployeeCoreRequestDTO} objects representing all employees.
     */
    @Override
    public PaginatedResponse<EmployeeCoreResponseDTO> findAllEmployeesByPage(int page, int pageSize) {
        PaginatedResponse<EmployeeCoreResponseDTO> response = new PaginatedResponse<>();
        List<EmployeeCoreResponseDTO> employeeCoreResponses = new ArrayList<>();

        UUID organizationId = organizationContext.getCurrentOrganizationId();
        
        // Get organization to find owner
        OrganizationEntity organization = OrganizationEntity.findById(organizationId);
        String ownerEmail = organization != null ? organization.owner : null;
        log.info("🔍 Owner email from organization: {}", ownerEmail);
        
        // Get all employees (without pagination first, to sort correctly)
        List<EmployeeCoreEntity> allEmployees = EmployeeCoreEntity.findEmployeesByOrgId(organizationId).list();
        
        log.info("📋 BEFORE SORTING:");
        allEmployees.forEach(e -> log.info("  - {} {}: companyEmail={}, personalEmail={}, id={}", 
            e.firstName, e.lastName, e.companyEmail, e.personalEmail, e.employeeId));
        
        // Sort: Owner first, then by employee creation order (using UUID which is time-based)
        allEmployees.sort((e1, e2) -> {
            // Check if employee is owner by comparing with both company email and personal email
            boolean e1IsOwner = ownerEmail != null && 
                (ownerEmail.equalsIgnoreCase(e1.companyEmail) || ownerEmail.equalsIgnoreCase(e1.personalEmail));
            boolean e2IsOwner = ownerEmail != null && 
                (ownerEmail.equalsIgnoreCase(e2.companyEmail) || ownerEmail.equalsIgnoreCase(e2.personalEmail));
            
            if (e1IsOwner && !e2IsOwner) return -1;
            if (!e1IsOwner && e2IsOwner) return 1;
            
            // Both are owner or both are not owner - sort by employeeId (creation time)
            return e1.employeeId.compareTo(e2.employeeId);
        });
        
        log.info("📋 AFTER SORTING:");
        allEmployees.forEach(e -> log.info("  - {} {}: companyEmail={}, personalEmail={}, id={}", 
            e.firstName, e.lastName, e.companyEmail, e.personalEmail, e.employeeId));
        
        // Apply pagination after sorting
        long totalItems = allEmployees.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allEmployees.size());
        List<EmployeeCoreEntity> employeeCoreEntities = allEmployees.subList(startIndex, endIndex);

        employeeCoreEntities.forEach(employeeCoreEntity -> {
            EmployeeCoreResponseDTO employeeResponse = getEmployeeFullDataResponse(employeeCoreEntity);
            employeeCoreResponses.add(employeeResponse);
        });

        response.setItems(employeeCoreResponses);
        response.setTotalItems(totalItems);
        response.setTotalPages(totalPages);
        response.setCurrentPage(page);
        response.setPageSize(pageSize);

        return response;
    }

    /**
     * Retrieves an employee from the database based on the given ID.
     *
     * @param employeeId The ID of the employee to retrieve.
     * @return The {@link EmployeeCoreRequestDTO} object representing the employee with the given ID.
     * @throws EntityNotFoundException If no employee with the given ID is found in the database.
     */
    @Override
    @Transactional
    public EmployeeCoreResponseDTO findEmployeeById(UUID employeeId) {
        if (employeeId == null) {
            throw new BadRequestException("id not given");
        }

        EmployeeCoreEntity employee = EmployeeCoreEntity.findById(employeeId);
        if (employee == null) {
            throw new EntityNotFoundException(ExceptionConstants.EMPLOYEE_NOT_FOUND);
        }

        EmployeeCoreResponseDTO employeeCoreResponseDTO = getEmployeeFullDataResponse(employee);
        // EmployeeProfileEntity employeeProfile = EmployeeProfileEntity.findById(employeeId);
        // if (employeeProfile != null) {
        // 	EmployeeProfileDTO employeeProfileDTO = objectMapper.convertValue(employeeProfile,
        // 	                                                                  EmployeeProfileDTO.class);
        // 	employeeCoreResponseDTO.setEmployeeProfile(employeeProfileDTO);
        // }
        return employeeCoreResponseDTO;
    }

    private EmployeeCoreResponseDTO getEmployeeFullDataResponse(EmployeeCoreEntity employee) {
        EmployeeCoreResponseDTO employeeResponse = new EmployeeCoreResponseDTO();

        employeeResponse.setEmployeeId(employee.employeeId);
        employeeResponse.setEmployeeStatus(employee.employeeStatus);
        employeeResponse.setCompanyEmail(employee.companyEmail);
        employeeResponse.setCompanyPhoneNumber(employee.companyPhoneNumber);
        employeeResponse.setFirstName(employee.firstName);
        employeeResponse.setLastName(employee.lastName);
        employeeResponse.setGender(employee.gender);
        employeeResponse.setDateOfBirth(employee.dateOfBirth);
        employeeResponse.setPersonalEmail(employee.personalEmail);
        employeeResponse.setPersonalPhoneNumber(employee.personalPhoneNumber);
        employeeResponse.setBirthPlace(employee.birthPlace);
        employeeResponse.setMaritalStatus(employee.maritalStatus);
        employeeResponse.setCurrentAddress(employee.currentAddress);

        // Check if employee has user account (use accountEmail as userId indicator)
        try {
            UserEntity userEntity = userRepository.find("employeeId", employee.employeeId).firstResult();
            if (userEntity != null && userEntity.getAccountEmail() != null) {
                // Use accountEmail as unique identifier for hasAccount check
                employeeResponse.setUserId(UUID.nameUUIDFromBytes(userEntity.getAccountEmail().getBytes()));
            }
        } catch (Exception e) {
            // Log and continue - userId will remain null if lookup fails
            log.warn("Failed to lookup user account for employeeId {}: {}", employee.employeeId, e.getMessage());
        }

        if (employee.jobTitleId != null) {
            JobTitleEntity jobTitle = JobTitleEntity.findById(employee.jobTitleId);
            if (jobTitle != null) {
                JobTitleDTO jobTitleDTO = new JobTitleDTO();
                jobTitleDTO.setJobTitleId(jobTitle.jobTitleId);
                jobTitleDTO.setTitle(jobTitle.title);
                employeeResponse.setJobTitle(jobTitleDTO);
            }
        }
        if (employee.teamId != null) {
            TeamEntity team = TeamEntity.findById(employee.teamId);
            if (team != null) {
                TeamDTO teamDTO = new TeamDTO();
                teamDTO.setTeamId(team.teamId);
                teamDTO.setName(team.name);
                employeeResponse.setTeam(teamDTO);

                if (team.departmentId != null) {
                    DepartmentEntity departmentEntity = DepartmentEntity.findById(team.departmentId);
                    if (departmentEntity != null) {
                        DepartmentDTO departmentDTO = new DepartmentDTO();
                        departmentDTO.setDepartmentId(departmentEntity.departmentId);
                        departmentDTO.setName(departmentEntity.name);
                        employeeResponse.setDepartment(departmentDTO);
                    }
                }
            }
        }

        return employeeResponse;
    }

    @Deprecated
    private JobTitleDTO findEmployeeJobTitle(UUID employeeId) {
        if (employeeId == null) {
            return null;
        }

        EmployeeJobTitleEntity employeeJobTitleEntity = EmployeeJobTitleEntity.findById(employeeId);
        if (employeeJobTitleEntity == null) {
            return null;
        }

        JobTitleEntity jobTitleEntity = JobTitleEntity.findById(employeeJobTitleEntity.jobId);
        if (jobTitleEntity == null) {
            return null;
        }

        return objectMapper.convertValue(jobTitleEntity, JobTitleDTO.class);
    }

    @Deprecated
    private TeamDTO findEmployeeTeam(UUID employeeId) {
        if (employeeId == null) {
            return null;
        }

        TeamMemberEntity teamMemberEntity = TeamMemberEntity.findById(employeeId);
        if (teamMemberEntity == null) {
            return null;
        }

        TeamEntity teamEntity = TeamEntity.findById(teamMemberEntity.teamId);
        if (teamEntity == null) {
            return null;
        }

        return objectMapper.convertValue(teamEntity, TeamDTO.class);
    }

    /**
     * Finds employees by job title ID.
     *
     * @param jobId The ID of the job title.
     * @return A list of employees who have the specified job title.
     * @throws EntityNotFoundException If the job title with the given ID is not found.
     */
    @Override
    public List<EmployeeCoreRequestDTO> findEmployeesByJobId(UUID jobId) {
        JobTitleDTO jobTitle = jobTitleService.findJobTitleById(jobId);
        if (jobTitle == null) {
            log.error("Could not find job title with given id: {}", jobId);
            throw new EntityNotFoundException(ExceptionConstants.JOB_TITLE_NOT_FOUND);
        }

        return null;
    }

    /**
     * Deletes an employee from the database based on the given ID.
     *
     * @param id The ID of the employee to delete.
     */
    @Override
    @Transactional
    public void deleteEmployeeById(UUID id) {
        if (id == null) {
            throw new BadRequestException("id not given");
        }

        EmployeeCoreEntity employeeCoreEntity = EmployeeCoreEntity.findById(id);
        if (employeeCoreEntity == null) {
            throw new EntityNotFoundException(ExceptionConstants.EMPLOYEE_NOT_FOUND);
        }
        
        // Delete associated user account if exists
        UserEntity userEntity = userRepository.find("employeeId", id).firstResult();
        if (userEntity != null) {
            log.info("Deleting user account for employee: {}", id);
            userRepository.delete(userEntity);
        }
        
        EmployeeCoreEntity.deleteById(id);
        log.info("Deleted employee with id: {}", id);
    }

    @Override
    public EmployeeCoreResponseDTO findEmployeeByAccountEmail(String accountEmail) {
        if (StringUtils.isEmpty(accountEmail)) {
            throw new BadRequestException("accountEmail not given");
        }

        EmployeeCoreEntity employee = EmployeeCoreEntity.findEmployeeByAccountEmail(accountEmail);
        if (employee == null) {
            log.error("Could not find employee with given accountEmail: {}", accountEmail);
            throw new EntityNotFoundException(ExceptionConstants.EMPLOYEE_NOT_FOUND);
        }

        return getEmployeeFullDataResponse(employee);
    }

    /**
     * DEV/TEST ONLY: Ensures an EmployeeCoreEntity exists for the given user.
     * If user.employeeId exists, returns it. Otherwise, creates a minimal EmployeeCoreEntity
     * and links it to the user account.
     *
     * @param user The UserEntity to ensure has an employee record
     * @return UUID of the employee (existing or newly created)
     */
    @Override
    @Transactional
    public UUID ensureEmployeeExistsForUser(UserEntity user) {
        if (user == null) {
            throw new BadRequestException("User cannot be null");
        }

        // If employeeId exists, return it
        if (user.employeeId != null) {
            return user.employeeId;
        }

        // DEV/TEST ONLY: Auto-create EmployeeCoreEntity if missing
        log.warn("DEV/TEST: Auto-creating EmployeeCoreEntity for user {} (employeeId was null)", user.getAccountEmail());

        UUID organizationId = organizationContext.getCurrentOrganizationId();
        if (organizationId == null) {
            throw new BadRequestException("Organization ID not found in context");
        }

        // Extract name from email (e.g., "john.doe@example.com" -> firstName: "John", lastName: "Doe")
        String accountEmail = user.getAccountEmail();
        String[] emailParts = accountEmail.split("@")[0].split("\\.");
        String firstName = emailParts.length > 0 ? capitalize(emailParts[0]) : "User";
        String lastName = emailParts.length > 1 ? capitalize(emailParts[1]) : "Account";

        // Create minimal EmployeeCoreEntity
        EmployeeCoreEntity employeeEntity = new EmployeeCoreEntity();
        employeeEntity.organizationId = organizationId;
        employeeEntity.firstName = firstName;
        employeeEntity.lastName = lastName;
        employeeEntity.companyEmail = accountEmail;
        employeeEntity.employeeStatus = EmployeeStatus.PROBATION;

        EmployeeCoreEntity.persist(employeeEntity);

        // Link employee to user
        user.employeeId = employeeEntity.employeeId;
        userRepository.persist(user);

        log.info("DEV/TEST: Created EmployeeCoreEntity {} for user {}", employeeEntity.employeeId, accountEmail);
        return employeeEntity.employeeId;
    }

    /**
     * Helper method to capitalize first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
