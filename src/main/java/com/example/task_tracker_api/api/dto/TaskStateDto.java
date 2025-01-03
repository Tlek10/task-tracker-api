package com.example.task_tracker_api.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStateDto {
    @NonNull
    private Long id;
    @NonNull
    private String name;

    @JsonProperty("left_task_state_id")
    private Long leftTaskStateId;
    @JsonProperty("right_task_state_id")
    private Long rightTaskStateId;

    @NonNull
    @JsonProperty("created_at")
    private Instant createdAt;

    @NonNull
    List<TaskDto> tasks;
}
