package org.microboy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class TeamMemberId implements Serializable {

    @Serial
    private static final long serialVersionUID = 6897373059339770478L;

    @Column(name = "team_id")
    private UUID teamId;

    @Column(name = "employee_id")
    private UUID employeeId;
}
