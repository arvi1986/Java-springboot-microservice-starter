package com.starter.sample.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StatusController {
    private static final Logger log = LoggerFactory.getLogger(StatusController.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus(
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        final String corrId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        log.info("Status endpoint called. X-Correlation-ID: {}", corrId);
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("timestamp", OffsetDateTime.now().format(ISO_FORMATTER));
        return ResponseEntity.ok(response);
    }
}
