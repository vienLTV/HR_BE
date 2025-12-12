package org.microboy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microboy.constants.ExceptionConstants;
import org.microboy.dto.DepartmentDTO;
import org.microboy.entity.DepartmentEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Duy Nguyen
 */
@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

	private final ObjectMapper objectMapper;

	/**
	 * Creates a new department.
	 *
	 * @param departmentDTO the department data transfer object containing
	 *                         information about the department to be created.
	 * @return the updated {@code DepartmentDTO} with the generated ID.
	 */
	@Override
	@Transactional
	public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
		if (departmentDTO == null) {
			throw new BadRequestException("Department request can not be null");
		}

		DepartmentEntity entity = objectMapper.convertValue(departmentDTO, DepartmentEntity.class);
		DepartmentEntity.persist(entity);

		DepartmentDTO createdDepartment = objectMapper.convertValue(entity, DepartmentDTO.class);
		log.info("New department created successfully: {}", createdDepartment);

		return createdDepartment;
	}

	/**
	 * Retrieves all departments.
	 *
	 * @return a list of all {@code DepartmentDTO} representing all departments.
	 */
	@Override
	public List<DepartmentDTO> findAllDepartment() {
		List<DepartmentEntity> departments = DepartmentEntity.listAll();
		return departments.stream()
		                  .map(departmentEntity -> objectMapper.convertValue(departmentEntity, DepartmentDTO.class))
		                  .collect(Collectors.toList());
	}

	/**
	 * Retrieves all sub-departments by their parent department ID.
	 *
	 * @param parentId the UUID of the parent department for which to find sub-departments.
	 * @return a list of {@code DepartmentDTO} representing sub-departments of the specified parent.
	 * @throws EntityNotFoundException if the parent department with the specified {@code parentId} does not exist.
	 */
	@Override
	public List<DepartmentDTO> findDepartmentByParentId(UUID parentId) {
		if (parentId == null) {
			throw new BadRequestException("Department parent id can not be null");
		}

		Optional<DepartmentEntity> parentDepartment = DepartmentEntity.findByIdOptional(parentId);
		if (parentDepartment.isEmpty()) {
			log.error("Could not find sub department because the parent department does not exist : {}", parentId);
			throw new EntityNotFoundException(ExceptionConstants.DEPARTMENT_NOT_FOUND);
		}

		List<DepartmentEntity> departments = DepartmentEntity.findByParentId(parentId);
		return departments.stream()
		                  .map(departmentEntity -> objectMapper.convertValue(departmentEntity, DepartmentDTO.class))
		                  .collect(Collectors.toList());
	}

	/**
	 * Retrieves a department by its ID.
	 *
	 * @param id the UUID of the department to be retrieved.
	 * @return a {@code DepartmentDTO} representing the department with the specified ID.
	 * @throws EntityNotFoundException  if no department is found with the given {@code id}.
	 * @throws IllegalArgumentException if {@code id} is {@code null}.
	 */
	@Override
	public DepartmentDTO findDepartmentById(UUID id) {
		if (id == null) {
			throw new BadRequestException("Department id can not be null");
		}

		DepartmentEntity entity = DepartmentEntity.findById(id);
		if (entity == null) {
			log.error("Could not find department with given id : {}", id);
			throw new EntityNotFoundException(ExceptionConstants.DEPARTMENT_NOT_FOUND);
		}
		return objectMapper.convertValue(entity, DepartmentDTO.class);
	}

	/**
	 * Updates an existing department.
	 *
	 * @param departmentDTO the {@code DepartmentDTO} containing updated information for the department.
	 * @param id               the UUID of the department to be updated.
	 * @return the updated {@code DepartmentDTO} after the changes have been applied.
	 * @throws EntityNotFoundException  if no department is found with the given {@code id}.
	 * @throws IllegalArgumentException if {@code DepartmentDTO} or {@code id} is {@code null}.
	 */
	@Override
	@Transactional
	public DepartmentDTO updateDepartment(DepartmentDTO departmentDTO, UUID id) {
		if (departmentDTO == null || id == null) {
			throw new BadRequestException("Department request is null or id not given");
		}

		DepartmentEntity existingDepartment = DepartmentEntity.findById(id);
		if (existingDepartment == null) {
			log.error("Could not find department with given id to update : {}", id);
			throw new EntityNotFoundException(ExceptionConstants.DEPARTMENT_NOT_FOUND);
		}

		existingDepartment.name = departmentDTO.getName();
		existingDepartment.description = departmentDTO.getDescription();
		existingDepartment.code = departmentDTO.getCode();
		existingDepartment.establishedDate = departmentDTO.getEstablishedDate();
		existingDepartment.parentId = departmentDTO.getParentId();
		existingDepartment.managerId = departmentDTO.getManagerId();
		existingDepartment.location = departmentDTO.getLocation();
		existingDepartment.phoneNumber = departmentDTO.getPhoneNumber();
		existingDepartment.email = departmentDTO.getEmail();
		log.info("Department updated successfully: {}", departmentDTO);

		return departmentDTO;
	}

	/**
	 * Deletes a department by its ID.
	 *
	 * @param id the UUID of the department to be deleted.
	 * @throws EntityNotFoundException  if no department is found with the given {@code id}.
	 * @throws IllegalArgumentException if {@code id} is {@code null}.
	 */
	@Override
	@Transactional
	public void deleteDepartmentById(UUID id) {
		if (id == null) {
			throw new BadRequestException("Department id can not be null");
		}

		Optional<DepartmentEntity> entity = DepartmentEntity.findByIdOptional(id);
		if (entity.isEmpty()) {
			log.error("Could not find department with given id to delete : {}", id);
			throw new EntityNotFoundException(ExceptionConstants.DEPARTMENT_NOT_FOUND);
		}

		DepartmentEntity.deleteById(id);
	}
}
