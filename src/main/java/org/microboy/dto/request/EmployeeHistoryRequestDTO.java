package org.microboy.dto.request;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import org.microboy.enums.EmploymentType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RegisterForReflection
public class EmployeeHistoryRequestDTO {

    private UUID employeeHistoryId;
    private UUID employeeId;
    private String companyName;
    private EmploymentType employmentType;
    private String jobTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String companyAddress;
}
