package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;
import org.microboy.enums.ContactType;

import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "emergency_contact")
public class EmergencyContactEntity extends PanacheEntityBase {

    @Id
    @UuidGenerator
    @Column(name = "emergency_contact_id")
    public UUID emergencyContactId;

    @Column(name = "employee_id")
    public UUID employeeId;

    @Column(name = "contact_name")
    public String contactName;

    @Column(name = "relationship")
    public String relationship;

    @Column(name = "daytime_number")
    public String daytimeNumber;

    @Column(name = "after_hours_number")
    public String afterHoursNumber;

    @Column(name = "contact_type")
    @Enumerated(EnumType.STRING)
    public ContactType contactType;

    @Column(name = "address")
    public String address;

    public static List<EmergencyContactEntity> findByEmployeeId(UUID employeeId) {
        return find("employeeId", employeeId).list();
    }

    public static Long getTotalItems(UUID employeeId) {
        return find("employeeId", employeeId).count();
    }

}
