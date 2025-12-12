package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;
import org.microboy.enums.EmploymentType;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "employee_history")
public class EmployeeHistoryEntity extends PanacheEntityBase {

    @Id
    @UuidGenerator
    @Column(name = "employee_history_id")
    public UUID employeeHistoryId;

    @Column(name = "employee_id")
    public UUID employeeId;

    @Column(name = "company_name")
    public String companyName;

    @Column(name = "employment_type")
    @Enumerated(EnumType.STRING)
    public EmploymentType employmentType;

    @Column(name = "job_title")
    public String jobTitle;

    @Column(name = "start_date")
    public LocalDate startDate;

    @Column(name = "end_date")
    public LocalDate endDate;

    @Column(name = "company_address")
    public String companyAddress;

    public static List<EmployeeHistoryEntity> getPageByEmployeeId(
        UUID employeeId,
        int page,
        int size
    ) {
        return find("employeeId", employeeId).page(page, size).list();
    }

    public static Long getTotalItems(UUID employeeId) {
        return find("employeeId", employeeId).count();
    }
}
