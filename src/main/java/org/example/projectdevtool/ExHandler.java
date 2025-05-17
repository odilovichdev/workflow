package org.example.projectdevtool;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
@AllArgsConstructor
public class ExHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> handleRuntimeEx(RuntimeException ex){
        return ResponseEntity
                .status(403)
                .body(new Response(ex.getMessage(), false));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Response> handleRuntimeEx(NoSuchElementException ex){
        return ResponseEntity
                .status(404)
                .body(new Response(ex.getMessage(), false));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response> handleRuntimeEx(IllegalArgumentException ex){
        return ResponseEntity
                .status(400)
                .body(new Response(ex.getMessage(), false));
    }
}
