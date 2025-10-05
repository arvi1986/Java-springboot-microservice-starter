package com.starter.sample.controller;

import com.starter.sample.dto.FolderDto;
import com.starter.sample.dto.ShareFolderRequest;
import com.starter.sample.service.FolderService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StorageControllerTest {
    @Mock
    private FolderService folderService;
    @InjectMocks
    private StorageController storageController;

    @Test
    void shareFolder_shouldReturnOk() {
        MockitoAnnotations.openMocks(this);
        ShareFolderRequest request = new ShareFolderRequest("/folder", List.of("user@example.com"));
        Principal principal = () -> "owner@example.com";
        FolderDto folderDto = mock(FolderDto.class);
        when(folderService.shareFolder(anyString(), anyList(), anyString())).thenReturn(folderDto);
        ResponseEntity<FolderDto> response = storageController.shareFolder(request, principal);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(folderDto, response.getBody());
    }
}


