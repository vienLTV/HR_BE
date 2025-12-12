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
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "team")
public class TeamEntity extends PanacheEntityBase {

    @Id
    @UuidGenerator
    @Column(name = "team_id")
    public UUID teamId;

    @Column(name = "name")
    public String name;

    @Column(name = "description")
    public String description;

    @Column(name = "department_id")
    public UUID departmentId;

    @Column(name = "established_date")
    public LocalDate establishedDate;

    @Column(name = "phone_number")
    public String phoneNumber;

    @Column(name = "email")
    public String email;

    @Column(name = "location")
    public String location;

}
