package com.example.task_tracker_api.api.controllers;

import com.example.task_tracker_api.api.controllers.helpers.ControllerHelper;
import com.example.task_tracker_api.api.dto.AckDto;
import com.example.task_tracker_api.api.dto.ProjectDto;
import com.example.task_tracker_api.api.exceptions.BadRequestException;
import com.example.task_tracker_api.api.exceptions.NotFoundException;
import com.example.task_tracker_api.api.factories.ProjectDtoFactory;
import com.example.task_tracker_api.store.entities.ProjectEntity;
import com.example.task_tracker_api.store.repositories.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@RequiredArgsConstructor
@RestController
public class ProjectController {
    private final ProjectRepository projectRepository;
    private final ProjectDtoFactory projectDtoFactory;
    private final ControllerHelper controllerHelper;
    public static final String FETCH_PROJECT = "api/projects";
    public static final String CREATE_OR_UPDATE_PROJECT = "api/projects";
    public static final String DELETE_PROJECT = "api/projects/{project_id}";

    @GetMapping(FETCH_PROJECT)
    public List<ProjectDto> fetchProjects(
            @RequestParam(value = "prefix_name", required = false)Optional<String> optionalPrefixName) {

       optionalPrefixName = optionalPrefixName.filter(prefixName ->!prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAllBy);

        return projectStream
                .map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

    @PutMapping(CREATE_OR_UPDATE_PROJECT)
    public ProjectDto createOrUpdateProject(
            @RequestParam(value = "project_id", required = false) Optional<Long> optionalProjectId,
            @RequestParam(value = "project_name", required = false) Optional<String> optionalProjectName
            // Другие параметры...
    ) {

        optionalProjectName = optionalProjectName.filter(projectName -> !projectName.trim().isEmpty());
        boolean isCreate = !optionalProjectId.isPresent();

        if (isCreate && !optionalProjectName.isPresent()) {
            throw new BadRequestException("Project name can't be empty");
        }

        final ProjectEntity project = optionalProjectId
                .map(controllerHelper::getProjectOrThrowException)
                .orElseGet(() -> ProjectEntity.builder().build());

        optionalProjectName.ifPresent(projectName -> {
            projectRepository
                    .findByName(projectName)
                    .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project.getId()))
                    .ifPresent(anotherProject -> {
                        throw new BadRequestException(
                                String.format("Project \"%s\" already exists.", projectName)
                        );
                    });
            project.setName(projectName);
        });

        final ProjectEntity savedProject = projectRepository.saveAndFlush(project);

        return projectDtoFactory.makeProjectDto(savedProject);
    }

    @DeleteMapping(DELETE_PROJECT)
    public AckDto deleteProject(
            @PathVariable(name = "project_id") Long projectId){
        controllerHelper.getProjectOrThrowException(projectId);
        projectRepository.deleteById(projectId);

        return AckDto.makeDefault(true);
    }
}
