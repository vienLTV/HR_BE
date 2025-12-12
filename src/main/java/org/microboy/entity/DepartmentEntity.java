package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@ToString
@Table(name = "department")
public class DepartmentEntity extends PanacheEntityBase {

    @Id
    @UuidGenerator
    @Column(name = "department_id")
    public UUID departmentId;

    @Column(name = "department_name")
    public String name;

    @Column(name = "department_code")
    public String code;

    @Column(name = "description")
    public String description;

    @Column(name = "manager_id")
    public UUID managerId;

    @Column(name = "location")
    public String location;

    @Column(name = "established_date")
    public LocalDate establishedDate;

    @Column(name = "phone_number")
    public String phoneNumber;

    @Column(name = "email")
    public String email;

    @Column(name = "parent_id")
    public UUID parentId;

    public static List<DepartmentEntity> findByParentId(UUID parentId) {
        return find("parentId", parentId).list();
    }
}
