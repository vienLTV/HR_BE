package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;
import org.microboy.enums.ExpiryStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bank_account")
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountEntity extends PanacheEntityBase {

    @Id
    @UuidGenerator
    @Column(name = "bank_account_id")
    public UUID bankAccountId;

    @Column(name = "account_holder_name")
    public String accountHolderName;

    @Column(name = "account_number")
    public String accountNumber;

    @Column(name = "bank_name")
    public String bankName;

    @Column(name = "issued_date")
    public LocalDate issuedDate;

    @Column(name = "expired_date")
    public LocalDate expiredDate;

    @Column(name = "expiry_status")
    @Enumerated(EnumType.STRING)
    public ExpiryStatus expiryStatus;

    @Column(name = "employee_id")
    public UUID employeeId;

    public static List<BankAccountEntity> findAllByEmployeeId(UUID employeeId) {
        return find("employeeId", employeeId).list();
    }

    public static BankAccountEntity findByEmployeeId(UUID employeeId) {
        return find("employeeId", employeeId).singleResult();
    }

    @PrePersist
    public void onPrePersist() {
        if (this.expiredDate != null && expiredDate.isBefore(LocalDate.now())) {
            this.expiryStatus = ExpiryStatus.EXPIRED;
        } else {
            this.expiryStatus = ExpiryStatus.VALID;
        }
    }

}
