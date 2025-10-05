package com.starter.sample.service;

import com.starter.sample.domain.Folder;
import com.starter.sample.domain.User;
import com.starter.sample.repository.FolderRepository;
import com.starter.sample.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FolderServiceDownloadTest {
    @Mock
    private FolderRepository folderRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private FolderService folderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void downloadFolderAsZip_shouldThrowException_whenFolderPathIsBlank() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            folderService.downloadFolderAsZip("", "user@example.com")
        );
        assertEquals("Folder path must not be blank", ex.getMessage());
    }

    @Test
    void downloadFolderAsZip_shouldThrowException_whenRequesterNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            folderService.downloadFolderAsZip("/folder", "user@example.com")
        );
        assertEquals("Requester not found", ex.getMessage());
    }
}

