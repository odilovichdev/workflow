package org.example.projectdevtool.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.example.projectdevtool.dto.AssignTaskToEmployeeDto;
import org.example.projectdevtool.dto.RateTaskDto;
import org.example.projectdevtool.dto.TaskRequestDto;
import org.example.projectdevtool.dto.TaskResponse;
import org.example.projectdevtool.entity.Task;
import org.example.projectdevtool.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@AllArgsConstructor
public class TaskController {


    private final TaskService taskService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody TaskRequestDto dto) {
        Task task = taskService.createTask(dto);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/assignTo-emp")
    @Operation(summary = "assign a task to a specific user(employee)")
    public ResponseEntity<TaskResponse> assignTaskToEmployee(@RequestBody AssignTaskToEmployeeDto dto) {
        TaskResponse response = taskService.assignTask(dto);
//        simpMessagingTemplate.convertAndSend("/task/message", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listBy-proId/{projectId}/{employeeId}")
    @Operation(summary = "get list of tasks",
                description = "give project and employee Id in path, it returns list of task in specific user"
    )
    public ResponseEntity<List<TaskResponse>> getTasksByProjectId(@PathVariable("projectId") Long projectId,
                                                                  @PathVariable("employeeId") Long employeeId) {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId, employeeId));
    }

    @PatchMapping("/update-status")
    @Operation(summary = "update a task status",
                parameters = {
                    @Parameter(name = "id", description = "task Id", required = true),
                    @Parameter(name = "status", required = true)
                }
    )
    public ResponseEntity<TaskResponse> updateStatusTask(@RequestParam("id") Long id,
                                              @RequestParam("status") String status) {
        TaskResponse task = taskService.updateStatusTask(id, status);
        simpMessagingTemplate.convertAndSend("/task/message", task);
        simpMessagingTemplate.convertAndSend("/emp-task/message", task);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/getAll/{projectId}") //
    @Operation(summary = "get all task of a project",
                description = "give projectId in path, it returns a list of tasks in a project"
    )
    public ResponseEntity<List<TaskResponse>> getAll(@PathVariable("projectId") Long projectId) {
//        return ResponseEntity.ok(taskService.getAll(projectId));
        return ResponseEntity.ok(taskService.findAll(projectId));
    }


    @PatchMapping("/rate/{projectOwnerId}")
    public ResponseEntity<TaskResponse> rateTask(@PathVariable("projectOwnerId") Long ownerId,
                                      @RequestBody RateTaskDto dto) {
        TaskResponse response = taskService.rateTask(ownerId, dto);
        simpMessagingTemplate.convertAndSend("/task/message", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/moveToBacklog/{taskId}")
    public ResponseEntity<TaskResponse> moveToBacklog(@PathVariable("taskId") Long taskId){
        TaskResponse response = taskService.moveToBacklog(taskId);
        simpMessagingTemplate.convertAndSend("/emp-task/message", response);
        simpMessagingTemplate.convertAndSend("/task/message", response);
        return ResponseEntity.ok(response);
    }
}
