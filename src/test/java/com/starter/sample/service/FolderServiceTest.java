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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FolderServiceTest {
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
    void shareFolder_shouldThrowException_whenFolderPathIsBlank() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                folderService.shareFolder("", List.of("test@example.com"), "owner@example.com")
        );
        assertEquals("Folder path must not be blank", ex.getMessage());
    }

    @Test
    void shareFolder_shouldThrowException_whenEmailsListIsEmpty() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                folderService.shareFolder("/folder", List.of(), "owner@example.com")
        );
        assertEquals("Emails list must not be empty", ex.getMessage());
    }

    @Test
    void shareFolder_shouldThrowException_whenOwnerNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                folderService.shareFolder("/folder", List.of("test@example.com"), "owner@example.com")
        );
        assertEquals("Owner not found", ex.getMessage());
    }

    @Test
    void shareFolder_shouldThrowException_whenFolderNotFound() {
        User owner = User.builder().id(1L).email("owner@example.com").name("Owner").roles(Set.of("ADMIN")).build();
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(folderRepository.findByPathAndOwner(anyString(), any())).thenReturn(Optional.empty());
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                folderService.shareFolder("/folder", List.of("test@example.com"), "owner@example.com")
        );
        assertEquals("Folder not found or not owned by user", ex.getMessage());
    }
}