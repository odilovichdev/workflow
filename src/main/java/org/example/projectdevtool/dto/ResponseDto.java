package org.example.projectdevtool.dto;

import lombok.Data;

@Data
public class ResponseDto {
    private String message;
    private boolean success;

    public ResponseDto(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
