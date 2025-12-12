package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.microboy.enums.TeamRole;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@Entity
@Table(name = "team_member")
public class TeamMemberEntity extends PanacheEntityBase {

    @Id
    @Column(name = "employee_id")
    public UUID employeeId;

    @Column(name = "team_id")
    public UUID teamId;

    @Column(name = "team_role")
    @Enumerated(EnumType.STRING)
    public TeamRole role;

}
