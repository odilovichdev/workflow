package org.example.projectdevtool.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.example.projectdevtool.dto.EmployeeToProject;
import org.example.projectdevtool.dto.ProjectRequestDto;
import org.example.projectdevtool.dto.ProjectResponse;
import org.example.projectdevtool.entity.Profile;
import org.example.projectdevtool.entity.Project;
import org.example.projectdevtool.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

@RestController
@RequestMapping("/api/pro")
@AllArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    @Operation(summary = "create a project",
                    description = "only DIRECTOR AND PM role can do",
                    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            required = true,
                            content = @Content(
                                    schema = @Schema(implementation = ProjectRequestDto.class)
                            )
                    )
    )
    public ResponseEntity<?> createProject(@RequestBody ProjectRequestDto dto) {
        return ResponseEntity.ok(projectService.createProject(dto));
    }

    @PostMapping("/add-emp")
    public ResponseEntity<?> addEmployeesToProject(@RequestBody EmployeeToProject dto) {
        return ResponseEntity.ok(projectService.addEmployees(dto));
    }

    @GetMapping("/get-list")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAll());
    }

    @PatchMapping("/update-status")
    @Operation(summary = "update status of a project",
                parameters = {
                        @Parameter(name = "id", description = "project Id", required = true),
                        @Parameter(name = "status", description = "String value", required = true)
                })
    public ResponseEntity<Project> updateProjectStatus(@RequestParam("id") Long id,
                                                       @RequestParam("status") String status) {
        return ResponseEntity.ok(projectService.updateStatus(id, status));
    }

    @PatchMapping("/delay/{id}")
    public ResponseEntity<?> delayProjectTime(@PathVariable("id") Long id,
                                              @RequestParam("startDate") LocalDate startDate,
                                              @RequestParam(value = "endDate", required = false) LocalDate endDate) {
        return ResponseEntity.ok(projectService.delayProjectTime(id, startDate, endDate));
    }

    @GetMapping("/list-today")
    public ResponseEntity<List<Project>> listOfTodayProjects() {
        return ResponseEntity.ok(projectService.getOfToday());
    }

    @GetMapping("/getById/{id}")
    @Operation(summary = "get a project with Id in path")
    public ResponseEntity<Project> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }
//    @GetMapping("/getById/{id}")
//    public Mono<ResponseEntity<Project>> getById(@PathVariable("id") Long id) {
//        return projectService.findById(id);
//    }

    @GetMapping("/findBy/empId/{id}")
    @Operation(summary = "find all projects which the user is in")
    public ResponseEntity<List<Project>> findByEmployeeList(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.findByEmployeeId(id));
    }

    @GetMapping("/emp-list/{projectId}/{ownerId}")
    @Operation(summary = "Get a list of employees in a project",
            description = "Returns a list of employees assigned to a specific project and owner",
            parameters = {
                    @Parameter(name = "projectId", description = "ID of the project", required = true, example = "1"),
                    @Parameter(name = "ownerId", description = "ID of the owner", required = true, example = "10")
            })
    public ResponseEntity<List<Profile>> getEmployeeList(@PathVariable("projectId") Long projectId,
                                                         @PathVariable("ownerId") Long ownerId) {
        return ResponseEntity.ok(projectService.getEmployeeList(projectId, ownerId));
    }


    @GetMapping("/all")
    @Operation(summary = "get all projects with limited fields")
    public ResponseEntity<List<ProjectResponse>> findAll() {
        return ResponseEntity.ok(projectService.findAll());
    }

}