package org.microboy.dto.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.microboy.enums.EmploymentType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RegisterForReflection
public class EmployeeHistoryResponseDTO {

    private UUID employeeHistoryId;
    private UUID employeeId;
    private String companyName;
    private EmploymentType employmentType;
    private String jobTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String companyAddress;
}
