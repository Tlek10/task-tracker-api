package com.example.task_tracker_api.api.controllers;

import com.example.task_tracker_api.api.controllers.helpers.ControllerHelper;
import com.example.task_tracker_api.api.dto.AckDto;
import com.example.task_tracker_api.api.dto.ProjectDto;
import com.example.task_tracker_api.api.dto.TaskStateDto;
import com.example.task_tracker_api.api.exceptions.BadRequestException;
import com.example.task_tracker_api.api.exceptions.NotFoundException;
import com.example.task_tracker_api.api.factories.TaskStateDtoFactory;
import com.example.task_tracker_api.store.entities.ProjectEntity;
import com.example.task_tracker_api.store.entities.TaskStateEntity;
import com.example.task_tracker_api.store.repositories.TaskStateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Transactional
@RequiredArgsConstructor
@RestController
public class TaskStateController {
    private final TaskStateRepository taskStateRepository;
    private final TaskStateDtoFactory taskStateDtoFactory;
    private final ControllerHelper controllerHelper;


    public static final String GET_TASK_STATES = "api/projects/{project_id}/task-states";
    public static final String CREATE_TASK_STATE = "api/projects/{project_id}/task-states";
    public static final String UPDATE_TASK_STATE = "api/task-states/{task_state_id}";
    public static final String DELETE_TASK_STATE = "api/task-states/{task_state_id}";

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

    @PostMapping(CREATE_TASK_STATE)
    public TaskStateDto createTaskState(
            @PathVariable(name = "project_id") Long projectId,
            @RequestParam(name = "task_state_name") String taskStateName) {

        if (taskStateName.trim().isEmpty()) {
            throw new BadRequestException("Task state name can't be empty");
        }

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        Optional<TaskStateEntity> optionalAnotherTaskState = Optional.empty();

        for (TaskStateEntity taskState: project.getTaskStates()){
            if (taskState.getName().equalsIgnoreCase(taskStateName)){

                throw new BadRequestException(String.format("Task state \"%s\" already exists.", taskStateName));
            }
            if (!taskState.getRightTaskState().isPresent()){
                optionalAnotherTaskState=Optional.of(taskState);
                break;
            }
        }

        TaskStateEntity taskState = taskStateRepository.saveAndFlush(
                TaskStateEntity.builder()
                        .name(taskStateName)
                        .project(project)
                        .build()

        );

        optionalAnotherTaskState
                .ifPresent(anotherTaskState -> {

                    taskState.setLeftTaskState(anotherTaskState);

                    anotherTaskState.setRightTaskState(taskState);

                    taskStateRepository.saveAndFlush(anotherTaskState);
                });

        final TaskStateEntity savedTaskState = taskStateRepository.saveAndFlush(taskState);

        return taskStateDtoFactory.makeTaskStateDto(savedTaskState);
    }

    @PatchMapping(UPDATE_TASK_STATE)
    public TaskStateDto updateTaskState(
            @PathVariable(name = "task_state_id") Long taskStateId,
            @RequestParam(name = "task_state_name") String taskStateName)
    {
        if (taskStateName.trim().isEmpty()) {
            throw new BadRequestException("Task state name can't be empty");
        }
        TaskStateEntity taskState = getTaskStateOrThrowException(taskStateId);

        taskStateRepository
                .findTaskStateEntityByProjectIdAndNameContainsIgnoreCase(
                        taskState.getProject().getId(),
                        taskStateName
                )
                .filter(anotherTaskState -> !anotherTaskState.getId().equals(taskStateId))
                .ifPresent(anotherTaskState->{
                    throw new BadRequestException(String.format("Task state \"%s\" already exists",taskStateName ));
                });

        taskState.setName(taskStateName);

        taskStateRepository.saveAndFlush(taskState);

        return taskStateDtoFactory.makeTaskStateDto(taskState);
    }

    @DeleteMapping(DELETE_TASK_STATE)
    public AckDto deleteTaskState(
            @PathVariable(name = "task_state_id") Long taskStateId){

        TaskStateEntity taskState = getTaskStateOrThrowException(taskStateId);

        taskStateRepository.deleteById(taskStateId);
        return AckDto.builder().answer(true).build();
    }

    private TaskStateEntity getTaskStateOrThrowException(Long taskStateId){

        return taskStateRepository
                .findById(taskStateId)
                .orElseThrow(()->
                        new NotFoundException(String.format("Task state with \"%s\" id dosen't exists", taskStateId)));
    }
}
