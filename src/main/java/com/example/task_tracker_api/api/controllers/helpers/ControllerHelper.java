package com.example.task_tracker_api.api.controllers.helpers;

import com.example.task_tracker_api.api.exceptions.NotFoundException;
import com.example.task_tracker_api.store.entities.ProjectEntity;
import com.example.task_tracker_api.store.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@FieldDefaults(makeFinal = true)
public class ControllerHelper {

    private final ProjectRepository projectRepository;

    public ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Project with \"%s\" doesn't exist", projectId
                                )
                        )
                );
    }
}
