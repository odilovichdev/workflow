package org.example.projectdevtool.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.example.projectdevtool.entity.Profile;
import org.example.projectdevtool.entity.Project;
import org.example.projectdevtool.entity.Status;
import org.example.projectdevtool.entity.Task;

import java.time.LocalDate;

@Data
public class TaskRequestDto {
    private String name;
    private String description;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;
    private Long projectId;
    private int score;
    private Task.Prior prior;
}
