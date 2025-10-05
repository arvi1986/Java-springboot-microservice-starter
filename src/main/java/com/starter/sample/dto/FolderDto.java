package com.starter.sample.dto;
;

import java.time.LocalDateTime;
import java.util.Set;

public record FolderDto(Long id, String path, UserDto owner, Set<UserDto> sharedWith, LocalDateTime createdAt, LocalDateTime updatedAt) {}

