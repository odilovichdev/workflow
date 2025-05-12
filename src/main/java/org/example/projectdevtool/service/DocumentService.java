package org.example.projectdevtool.service;

import lombok.AllArgsConstructor;
import org.example.projectdevtool.repo.DocumentRepo;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DocumentService {

    private final DocumentRepo documentRepo;
}
