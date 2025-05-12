package org.example.projectdevtool.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeHistory {
    private Long id;
    private String firstname;
    private String lastname;
    private String profession;
    private List<String> projects;
    private int rating;
}
