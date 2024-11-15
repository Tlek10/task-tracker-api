package com.example.task_tracker_api.api.factories;

import com.example.task_tracker_api.api.dto.TaskDto;
import com.example.task_tracker_api.api.dto.TaskStateDto;
import com.example.task_tracker_api.store.entities.TaskStateEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TaskStateDtoFactory {

    TaskDtoFactory taskDtoFactory;

    public TaskStateDto makeTaskStateDto(TaskStateEntity entity){
        return TaskStateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .ordinal(entity.getOrdinal())
                .tasks(entity
                        .getTasks()
                        .stream()
                        .map(taskDtoFactory::makeTaskDto)
                        .collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt())
                .build();

    }
}
