package org.microboy.dto.request;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RegisterForReflection
public class EmployeeHistoryRequestDTO {

    private UUID employeeId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private String changeType;
    private String changedBy;
    private Instant changedAt;
}
