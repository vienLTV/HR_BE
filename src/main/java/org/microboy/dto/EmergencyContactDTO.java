package org.microboy.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import org.microboy.enums.ContactType;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RegisterForReflection
public class EmergencyContactDTO {

    private UUID emergencyContactId;
    private UUID employeeId;
    private String contactName;
    private String relationship;
    private String daytimeNumber;
    private String afterHoursNumber;
    private ContactType contactType;
    private String address;

}
