package org.example.projectdevtool.dto;

public record UpdateRequest(
        Long projectId,
        String status
) {
}
