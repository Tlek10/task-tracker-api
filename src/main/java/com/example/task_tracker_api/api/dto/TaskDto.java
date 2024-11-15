package com.example.task_tracker_api.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    @NonNull
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String description;

    @NonNull
    @JsonProperty("created_at")
    private Instant createdAt;
}
