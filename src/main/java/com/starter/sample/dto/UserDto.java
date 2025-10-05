package com.starter.sample.dto;

import java.util.Set;

public record UserDto(Long id, String email, String name, Set<String> roles) {}

