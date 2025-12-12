package org.microboy.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RegisterForReflection
@ToString
public class JobTitleDTO {

    private UUID jobTitleId;
    private String title;
    private String description;
}
