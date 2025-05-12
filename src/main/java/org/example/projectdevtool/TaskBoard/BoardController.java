package org.example.projectdevtool.TaskBoard;

import lombok.AllArgsConstructor;
import org.example.projectdevtool.dto.TaskResponse;
import org.example.projectdevtool.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@AllArgsConstructor
public class BoardController {

    private final TaskService taskService;

    @GetMapping("/get/{projectId}/{userId}")
    public ResponseEntity<List<TaskResponse>> getFromBoard(@PathVariable Long projectId,
                                                           @PathVariable Long userId){
        return ResponseEntity.ok(taskService.getFromBoard(projectId, userId));
    }
}
