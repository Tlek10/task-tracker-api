package com.example.task_tracker_api.api.controllers;

import com.example.task_tracker_api.api.controllers.helpers.ControllerHelper;
import com.example.task_tracker_api.api.dto.ProjectDto;
import com.example.task_tracker_api.api.dto.TaskStateDto;
import com.example.task_tracker_api.api.factories.TaskStateDtoFactory;
import com.example.task_tracker_api.store.entities.ProjectEntity;
import com.example.task_tracker_api.store.repositories.TaskStateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@RequiredArgsConstructor
@RestController
public class TaskStateController {
    private final TaskStateRepository taskStateRepository;
    private final TaskStateDtoFactory taskStateDtoFactory;
    private final ControllerHelper controllerHelper;


    public static final String GET_TASK_STATES = "api/projects/{project_id}/task-states";
    public static final String CREATE_OR_UPDATE_PROJECT = "api/projects";
    public static final String DELETE_PROJECT = "api/projects/{project_id}";

    @GetMapping(GET_TASK_STATES)
    public List<TaskStateDto> getTaskStates(
            @PathVariable(name = "project_id") Long projectId) {

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        return project
                .getTaskStates()
                .stream()
                .map(taskStateDtoFactory::makeTaskStateDto)
                .collect(Collectors.toList());
    }
}
