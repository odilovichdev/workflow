package org.example.projectdevtool.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.projectdevtool.dto.AssignTaskToEmployeeDto;
import org.example.projectdevtool.dto.RateTaskDto;
import org.example.projectdevtool.dto.TaskRequestDto;
import org.example.projectdevtool.dto.TaskResponse;
import org.example.projectdevtool.entity.*;
import org.example.projectdevtool.repo.ProfileRepo;
import org.example.projectdevtool.repo.ProjectRepo;
import org.example.projectdevtool.repo.TaskRepo;
import org.example.projectdevtool.repo.UsersRepo;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepo taskRepo;
    private final ProjectRepo projectRepo;
    private final ProfileRepo profileRepo;
    private final EmailService emailService;
    private final UsersRepo usersRepo;


    public Task createTask(TaskRequestDto dto) {
        Task task = new Task();
        Project project = projectRepo.findById(dto.getProjectId())
                .orElseThrow(() -> new NoSuchElementException("not found"));

        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setProject(project);
        task.setPrior(dto.getPrior());
        task.setStartDate(dto.getStartDate());
        task.setEndDate(dto.getEndDate());
        if (dto.getStartDate().isBefore(LocalDate.now())) {
            task.setStatus(Status.PENDING);
        } else if (dto.getStartDate().isEqual(LocalDate.now())) {
            task.setStatus(Status.IN_PROGRESS);
        } else {
            task.setStatus(Status.PENDING);
        }

        task.setScore(0);
        return taskRepo.save(task);
    }

    @Transactional
    public TaskResponse assignTask(AssignTaskToEmployeeDto dto) {
        Task task = taskRepo.findById(dto.getTaskId())
                .orElseThrow(() -> new NoSuchElementException("not found"));

        Users user = usersRepo.findById(dto.getUserId() )
                .orElseThrow(()->new NoSuchElementException("user not found"));

        Profile profile = profileRepo.findByUser(user);

        task.setAssignedTo(profile);
        task.setAssignedAt(LocalDateTime.now());

        emailService.notifyForTaskAssignment(profile.getUser().getEmail(),
                task.getName() + " assigned to you",
                "Hi " + profile.getFirstname() + ",\nyou have been assigned for " + task.getName() + ", please check out your account");

        taskRepo.save(task);
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getProject().getName(),
                task.getAssignedTo().getUser().getEmail(),
                task.getStatus(),
                task.getPrior().toString(),
                task.getProject().getOwner().getId(),
                task.getScore()
        );
    }

    public List<TaskResponse> getTasksByProjectId(Long projectId, Long employeeId) {

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("project not found"));

        Users user = usersRepo.findById(employeeId)
                .orElseThrow(()->new NoSuchElementException("user not found"));

        Profile profile = profileRepo.findByUser(user);

        List<Task> tasks = taskRepo.findByProjectAndAssignedTo(project, profile);
        tasks.sort(Comparator
                .comparing(Task::getStatus)
                .thenComparing(Task::getStartDate));

        List<TaskResponse> responses = new ArrayList<>();
        for (Task task : tasks) {
            TaskResponse response = new TaskResponse(
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    task.getProject().getName(),
                    task.getAssignedTo().getUser().getEmail(),
                    task.getStatus(),
                    task.getPrior().toString(),
                    task.getProject().getOwner().getId(),
                    task.getScore()
            );

            responses.add(response);
        }
        return responses;
    }

    public TaskResponse updateStatusTask(Long id, String status) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("not found"));

        if (status.equals("START") && task.getStatus().equals(Status.PENDING)) {
            task.setStatus(Status.IN_PROGRESS);
            task.setStartDate(LocalDate.now());
        } else if (status.equals("START") && task.getStatus().equals(Status.BACKLOG)) {
            task.setStatus(Status.IN_PROGRESS);
            task.setStartDate(LocalDate.now());
        } else if (status.equals("CANCEL")) {
            task.setStatus(Status.CANCELLED);
        } else if (status.equals("FINISH") && task.getStatus().equals(Status.IN_PROGRESS)) {
            task.setStatus(Status.COMPLETED);
            task.setEndDate(LocalDate.now());
        } else if (status.equals("BACKLOG")) {
            task.setStatus(Status.IN_PROGRESS);
            task.setStartDate(LocalDate.now());
        }

        taskRepo.save(task);
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getProject().getName(),
                task.getAssignedTo().getUser().getEmail(),
                task.getStatus(),
                task.getPrior().toString(),
                task.getProject().getOwner().getId(),
                task.getScore()
        );
    }

//    public List<TaskResponse> getAll(Long projectId) {
//        Project project = projectRepo.findById(projectId)
//                .orElseThrow(() -> new NoSuchElementException("not found"));
//
//        Profile employee = profileRepo.findById(empId)
//                .orElseThrow(() -> new NoSuchElementException("not found"));
//
//        if (project.getEmployees().contains(employee)) {
//            List<TaskResponse> tasks = taskRepo.findByProject(project).stream().map(
//                    task -> new TaskResponse(
//                            task.getId(),
//                            task.getName(),
//                            task.getDescription(),
//                            task.getProject().getName(),
//                            task.getAssignedTo().getUser().getEmail(),
//                            task.getStatus(),
//                            task.getPrior().toString(),
//                            task.getProject().getOwner().getId(),
//                            task.getScore()
//                    )
//            ).toList();
//            return tasks;
//        } else
//            throw new RuntimeException("permission denied");
//    }

    public TaskResponse rateTask(Long ownerId, RateTaskDto dto) {
        Task task = taskRepo.findById(dto.getCurrentTaskId())
                .orElseThrow(()->new NoSuchElementException("not found"));

        if (task.getStatus().toString().equals("COMPLETED")
        && task.getProject().getOwner().getId().equals(ownerId)){
            task.setScore(dto.getRate());
            taskRepo.save(task);

            return new TaskResponse(
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    task.getProject().getName(),
                    task.getAssignedTo().getUser().getEmail(),
                    task.getStatus(),
                    task.getPrior().toString(),
                    task.getProject().getOwner().getId(),
                    task.getScore()
            );
        }
        else
            throw new RuntimeException("error with rating");
    }

    public TaskResponse moveToBacklog(Long taskId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(()-> new NoSuchElementException("not found"));

        if (task.getStatus().toString().equals("COMPLETED")){
            task.setStatus(Status.BACKLOG);
            taskRepo.save(task);
            return new TaskResponse(
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    task.getProject().getName(),
                    task.getAssignedTo().getUser().getEmail(),
                    task.getStatus(),
                    task.getPrior().toString(),
                    task.getProject().getOwner().getId(),
                    task.getScore()
            );
        }
        else
            throw new RuntimeException("error");
    }

    public List<TaskResponse> getFromBoard(Long projectId, Long userId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));

        boolean isEmployee = project.getEmployees().stream()
                .anyMatch(profile -> profile.getUser().getId().equals(userId));

        if (!isEmployee) {
            throw new RuntimeException("User is not part of the project");
        }

        List<Task> tasks = taskRepo.findByProject(project);

        return tasks.stream()
                .map(task -> new TaskResponse(task.getId(),
                        task.getName(),
                        task.getDescription(),
                        task.getProject().getName(),
                        task.getAssignedTo().getUser().getEmail(),
                        task.getStatus(),
                        task.getPrior().toString(),
                        task.getProject().getOwner().getId(),
                        task.getScore())) // Assuming a constructor for TaskResponse that takes Task
                .collect(Collectors.toList());
    }

    public List<TaskResponse> findAll(Long projectId) {

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));

        List<Task> tasks = taskRepo.findByProject(project);

        return tasks.stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getName(),
                        task.getDescription(),
                        task.getProject().getName(),
                        task.getAssignedTo() != null ? task.getAssignedTo().getUser().getEmail() : null, // Handle null
                        task.getStatus(),
                        task.getPrior().toString(),
                        task.getProject().getOwner().getId(),
                        task.getScore()
                ))
                .collect(Collectors.toList());
    }

}
