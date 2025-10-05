package com.starter.sample.dto;

import java.time.LocalDateTime;

public record FileDto(Long id, String name, String path, Long size, LocalDateTime createdAt, LocalDateTime updatedAt) {}




