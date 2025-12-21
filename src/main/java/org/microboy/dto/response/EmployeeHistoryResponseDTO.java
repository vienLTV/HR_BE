package org.microboy.dto.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RegisterForReflection
public class EmployeeHistoryResponseDTO {

    private UUID id;
    private UUID employeeId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private String changeType;
    private String changedBy;
    private Instant changedAt;
}
