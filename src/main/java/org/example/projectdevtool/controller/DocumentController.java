package org.example.projectdevtool.controller;

import lombok.AllArgsConstructor;
import org.example.projectdevtool.dto.DocRequest;
import org.example.projectdevtool.entity.Document;
import org.example.projectdevtool.entity.Project;
import org.example.projectdevtool.entity.Users;
import org.example.projectdevtool.repo.DocumentRepo;
import org.example.projectdevtool.repo.ProjectRepo;
import org.example.projectdevtool.repo.UsersRepo;
import org.example.projectdevtool.service.DocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/doc")
@AllArgsConstructor
public class DocumentController {

private final DocumentRepo documentRepo;
private final UsersRepo usersRepo;
private final ProjectRepo projectRepo;

    @PostMapping("/upload/{userId}/{projectId}")
    public ResponseEntity<String> uploadDocument(
            @PathVariable Long userId,
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file) {
        try {
            byte[] fileData = file.getBytes();

            Document document = new Document();
            document.setFilename(file.getOriginalFilename());
            document.setContentType(file.getContentType());
            document.setData(fileData);

            Project project = projectRepo.findById(projectId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Project not found"));

            Users users = usersRepo.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "User not found"));

            document.setProject(project);
            document.setUploadedBy(users);
            document.setUploadedAt(LocalDateTime.now());

            documentRepo.save(document);

            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while processing the file.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getDocument(@PathVariable Long id) {
        Document document = documentRepo.findById(id)
                .orElseThrow(()->new NoSuchElementException("file not found"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .body(document.getData());
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<byte[]> viewDocument(@PathVariable Long id) {
        Document document = documentRepo.findById(id)
                .orElseThrow(()->new NoSuchElementException("file not found"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .body(document.getData());
    }

}
