package org.microboy.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.microboy.enums.ExpiryStatus;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@RegisterForReflection
public class BankAccountDTO {
	private UUID bankAccountId;
	private String accountHolderName;
	private String accountNumber;
	private String bankName;
	private LocalDate issuedDate;
	private LocalDate expiredDate;
	private ExpiryStatus expiryStatus;
	private UUID employeeId;
}
