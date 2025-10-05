package com.starter.sample.controller;

import com.starter.sample.dto.ShareFolderRequest;
import com.starter.sample.dto.FolderDto;
import com.starter.sample.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {
    private static final Logger log = LoggerFactory.getLogger(StorageController.class);
    private final FolderService folderService;

    @PostMapping("/share")
    public ResponseEntity<FolderDto> shareFolder(@RequestBody ShareFolderRequest request, Principal principal) {
        String ownerEmail = principal.getName();
        FolderDto result = folderService.shareFolder(request.folderpath(), request.emails(), ownerEmail);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadFolder(@RequestParam String folderpath, Principal principal) {
        String requesterEmail = principal.getName();
        byte[] zipBytes = folderService.downloadFolderAsZip(folderpath, requesterEmail);
        StreamingResponseBody responseBody = outputStream -> outputStream.write(zipBytes);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=folder.zip")
                .body(responseBody);
    }
}


