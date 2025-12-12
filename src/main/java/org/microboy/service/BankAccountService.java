package org.microboy.service;

import org.microboy.dto.BankAccountDTO;

import java.util.List;
import java.util.UUID;

public interface BankAccountService {

    BankAccountDTO createBankAccount(BankAccountDTO bankAccountDTO);

    List<BankAccountDTO> findAllBankAccount();

    List<BankAccountDTO> findBankAccountByEmployeeId(UUID employeeId);

    BankAccountDTO updateBankAccount(BankAccountDTO bankAccountDTO, UUID id);

    void deleteBankAccountById(UUID id);
}
