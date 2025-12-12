package org.microboy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microboy.constants.ExceptionConstants;
import org.microboy.dto.CertificateDTO;
import org.microboy.entity.CertificateEntity;
import org.microboy.entity.EmployeeCoreEntity;

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
public class CertificateServiceImpl implements CertificateService {

	private final ObjectMapper objectMapper;

	/**
	 * Retrieves all certificates from the repository and converts each {@link CertificateEntity} to a {@link CertificateDTO}.
	 *
	 * @return a list of {@link CertificateDTO} representing all certificates found in the repository
	 */
	@Override
	public List<CertificateDTO> findAllCertificate() {
		List<CertificateEntity> certificates = CertificateEntity.listAll();
		return certificates.stream()
		                   .map(certificateEntity -> objectMapper.convertValue(certificateEntity,
		                                                                       CertificateDTO.class))
		                   .collect(Collectors.toList());
	}

	/**
	 * Finds all certificates associated with the employee identified by the given {@code employeeId}.
	 *
	 * @param employeeId the unique identifier of the employee whose certificates are to be found
	 * @return a list of {@link CertificateDTO} representing all certificates associated with the employee
	 * @throws EntityNotFoundException if no employee with the given {@code employeeId} exists in the repository
	 */
	@Override
	public List<CertificateDTO> findCertificateByEmployeeId(UUID employeeId) {
		Optional<EmployeeCoreEntity> employeeEntity = EmployeeCoreEntity.findByIdOptional(employeeId);
		if (employeeEntity.isEmpty()) {
			log.error("Could not find employee with given id: {}", employeeId);
			throw new EntityNotFoundException(ExceptionConstants.CERTIFICATE_NOT_FOUND);
		}

		List<CertificateEntity> certificates = CertificateEntity.findAllByEmployeeId(employeeId);
		return certificates.stream()
		                   .map(certificateEntity -> objectMapper.convertValue(certificateEntity,
		                                                                       CertificateDTO.class))
		                   .collect(Collectors.toList());
	}


	/**
	 * Creates a new certificate.
	 *
	 * @param certificateDTO the data transfer object representing the certificate to be created
	 * @return the created certificate as a {@link CertificateDTO}
	 */
	@Override
	@Transactional
	public CertificateDTO createCertificate(CertificateDTO certificateDTO) {
		if (certificateDTO == null) {
			throw new BadRequestException("Certificate request cannot be null");
		}

		CertificateEntity entity = objectMapper.convertValue(certificateDTO, CertificateEntity.class);
		CertificateEntity.persist(entity);

		CertificateDTO createdCertificate = objectMapper.convertValue(entity, CertificateDTO.class);
		log.info("New certificate created successfully: {}", createdCertificate);

		return createdCertificate;
	}

	/**
	 * Finds a certificate by its unique identifier.
	 *
	 * @param id the unique identifier of the certificate to find
	 * @return the {@link CertificateDTO} representing the found certificate
	 * @throws EntityNotFoundException if no certificate with the given {@code id} exists in the repository
	 */
	@Override
	public CertificateDTO findCertificateById(UUID id) {
		if (id == null) {
			throw new BadRequestException("id request cannot be null");
		}

		CertificateEntity entity = CertificateEntity.findById(id);
		if (entity == null) {
			log.error("Could not find certificate with given id: {}", id);
			throw new EntityNotFoundException(ExceptionConstants.CERTIFICATE_NOT_FOUND);
		}

		return objectMapper.convertValue(entity, CertificateDTO.class);
	}

	/**
	 * Updates an existing certificate identified by the given ID with the data from the provided {@link CertificateDTO}.
	 *
	 * @param certificateDTO the data transfer object containing the updated certificate information
	 * @param id                the unique identifier of the certificate to update
	 * @return the updated {@link CertificateDTO} representing the updated certificate
	 * @throws EntityNotFoundException if no certificate with the given {@code id} exists in the repository
	 */
	@Override
	@Transactional
	public CertificateDTO updateCertificate(CertificateDTO certificateDTO, UUID id) {
		if (certificateDTO == null || id == null) {
			throw new BadRequestException("certificate request cannot be null or id can not be empty");
		}

		CertificateEntity entity = CertificateEntity.findById(id);
		if (entity == null) {
			log.error("Could not find certificate with given id to update: {}", id);
			throw new EntityNotFoundException(ExceptionConstants.CERTIFICATE_NOT_FOUND);
		}

		entity.name = certificateDTO.getName();
		entity.certificateImg = certificateDTO.getCertificateImg();
		entity.description = certificateDTO.getDescription();
		entity.expiredDate = certificateDTO.getExpiredDate();
		entity.level = certificateDTO.getLevel();
		entity.employeeId = certificateDTO.getEmployeeId();
		entity.licenseCode = certificateDTO.getLicenseCode();
		entity.provider = certificateDTO.getProvider();
		entity.issuedDate = certificateDTO.getIssuedDate();
		log.info("Certificate updated successfully: {}", certificateDTO);

		return certificateDTO;
	}

	/**
	 * Deletes a CertificateEntity from the database by its ID.
	 * If no CertificateEntity with the given ID is found, an EntityNotFoundException is thrown.
	 *
	 * @param id The ID of the CertificateEntity to delete.
	 * @throws EntityNotFoundException If no CertificateEntity with the given ID is found.
	 */
	@Override
	@Transactional
	public void deleteCertificateById(UUID id) {
		if (id == null) {
			throw new BadRequestException("id request cannot be null");
		}

		Optional<CertificateEntity> entity = CertificateEntity.findByIdOptional(id);
		if (entity.isEmpty()) {
			log.error("Could not find certificate with given id to delete: {}", id);
			throw new EntityNotFoundException(ExceptionConstants.CERTIFICATE_NOT_FOUND);
		}

		CertificateEntity.deleteById(id);
		log.info("Certificate with given id deleted successfully: {}", id);
	}
}
