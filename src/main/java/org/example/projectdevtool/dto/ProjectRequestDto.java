package org.example.projectdevtool.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.projectdevtool.entity.Project;

import java.time.LocalDate;

@Data
public class ProjectRequestDto {
    private Long ownerId;
    private String name;
    private String description; // Renamed from 'desc'
    private double budget;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;

}
