package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.microboy.enums.EmploymentType;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee_history")
public class EmployeeHistoryEntity extends PanacheEntityBase {

    @Id
    @UuidGenerator
    @Column(name = "employee_history_id")
    public UUID employeeHistoryId;

    @Column(name = "employee_id")
    public UUID employeeId;

    @Column(name = "field_name")
    public String fieldName;

    @Column(name = "old_value")
    public String oldValue;

    @Column(name = "new_value")
    public String newValue;

    @Column(name = "change_type")
    public String changeType;

    @Column(name = "changed_by")
    public String changedBy;

    @Column(name = "changed_at")
    public Instant changedAt;

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
