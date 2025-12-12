package org.microboy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microboy.constants.ExceptionConstants;
import org.microboy.dto.BankAccountDTO;
import org.microboy.entity.BankAccountEntity;
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
public class BankAccountServiceImpl implements BankAccountService {

	private final ObjectMapper objectMapper;

	/**
	 * Creates a new BankAccount based on the provided BankAccountDTO.
	 *
	 * @param bankAccountDTO The DTO object containing data for the new BankAccount.
	 * @return The DTO representation of the newly created BankAccount.
	 */
	@Override
	@Transactional
	public BankAccountDTO createBankAccount(BankAccountDTO bankAccountDTO) {
		if (bankAccountDTO == null) {
			throw new BadRequestException("BankAccountDTO is null");
		}

		BankAccountEntity entity = objectMapper.convertValue(bankAccountDTO, BankAccountEntity.class);
		BankAccountEntity.persist(entity);

		BankAccountDTO createdBankAccount = objectMapper.convertValue(entity, BankAccountDTO.class);
		log.info("Add new bank account successfully: {}", entity.bankAccountId);

		return createdBankAccount;
	}

	/**
	 * Retrieves all bank accounts from the database and returns their DTO representations.
	 *
	 * @return A list of BankAccountDTO objects representing all bank accounts in the database.
	 */
	@Override
	public List<BankAccountDTO> findAllBankAccount() {
		List<BankAccountEntity> bankAccounts = BankAccountEntity.listAll();
		return bankAccounts.stream()
		                   .map(bankAccountEntity -> objectMapper.convertValue(bankAccountEntity,
		                                                                       BankAccountDTO.class))
		                   .collect(Collectors.toList());
	}

	/**
	 * Retrieves all bank accounts associated with the employee specified by {@code employeeId}.
	 * If no employee is found with the given ID, an {@link EntityNotFoundException} is thrown.
	 *
	 * @param employeeId The ID of the employee whose bank accounts are to be retrieved.
	 * @return A list of BankAccountDTO objects representing bank accounts associated with the employee.
	 * @throws EntityNotFoundException If no employee is found with the specified employeeId.
	 */
	@Override
	public List<BankAccountDTO> findBankAccountByEmployeeId(UUID employeeId) {
		if (employeeId == null) {
			throw new BadRequestException("employeeId is null");
		}

		Optional<EmployeeCoreEntity> employeeEntity = EmployeeCoreEntity.findByIdOptional(employeeId);
		if (employeeEntity.isEmpty()) {
			log.error("Could not find bank account because employee does not exist : {}", employeeId);
			throw new EntityNotFoundException(ExceptionConstants.BANK_ACCOUNT_NOT_FOUND);
		}

		List<BankAccountEntity> bankAccounts = BankAccountEntity.findAllByEmployeeId(employeeId);
		return bankAccounts.stream()
		                   .map(bankAccountEntity -> objectMapper.convertValue(bankAccountEntity,
		                                                                       BankAccountDTO.class))
		                   .collect(Collectors.toList());
	}

	/**
	 * Updates a bank account entity with the provided data.
	 *
	 * @param bankAccountDTO The DTO containing updated information.
	 * @param id                The ID of the bank account entity to update.
	 * @return The updated bank account DTO after persistence.
	 * @throws EntityNotFoundException if no bank account entity with the given ID exists.
	 */
	@Override
	@Transactional
	public BankAccountDTO updateBankAccount(BankAccountDTO bankAccountDTO, UUID id) {
		if (bankAccountDTO == null || id == null) {
			throw new BadRequestException("BankAccountDTO is null or id is null");
		}

		BankAccountEntity bankAccountEntity = BankAccountEntity.findById(id);
		if (bankAccountEntity == null) {
			log.error("Could not find bank account with given id to update : {}", id);
			throw new EntityNotFoundException(ExceptionConstants.BANK_ACCOUNT_NOT_FOUND);
		}

		bankAccountEntity.accountNumber = bankAccountDTO.getAccountNumber();
		bankAccountEntity.accountHolderName = bankAccountDTO.getAccountHolderName();
		bankAccountEntity.bankName = bankAccountDTO.getBankName();
		bankAccountEntity.employeeId = bankAccountDTO.getEmployeeId();
		bankAccountEntity.expiredDate = bankAccountDTO.getExpiredDate();
		bankAccountEntity.issuedDate = bankAccountDTO.getIssuedDate();
		log.info("Update bank account successfully: {}", bankAccountEntity.bankAccountId);

		return bankAccountDTO;
	}

	/**
	 * Deletes a bank account entity by its ID.
	 *
	 * @param id The ID of the bank account entity to delete.
	 * @throws EntityNotFoundException if no bank account entity with the given ID exists.
	 */
	@Override
	@Transactional
	public void deleteBankAccountById(UUID id) {
		if (id == null) {
			throw new BadRequestException("id is null");
		}

		Optional<BankAccountEntity> entity = BankAccountEntity.findByIdOptional(id);
		if (entity.isEmpty()) {
			log.error("Could not find bank account with given id to delete : {}", id);
			throw new EntityNotFoundException(ExceptionConstants.BANK_ACCOUNT_NOT_FOUND);
		}

		BankAccountEntity.deleteById(id);
	}
}
