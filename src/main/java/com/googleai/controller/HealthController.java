package com.googleai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Sample REST controller — endpoints here are auto-scanned and listed
 * in the generated README.md after every build.
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    // ── Health ──────────────────────────────────────────────────────────────

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ── Info ────────────────────────────────────────────────────────────────

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        return ResponseEntity.ok(Map.of(
                "application", "GoogleAi",
                "description", "Spring Boot project with Google AI integration",
                "java", System.getProperty("java.version")
        ));
    }

    // ── Echo ────────────────────────────────────────────────────────────────

    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "echo", body,
                "receivedAt", LocalDateTime.now().toString()
        ));
    }

    // ── Greet ───────────────────────────────────────────────────────────────

    @GetMapping("/greet/{name}")
    public ResponseEntity<Map<String, String>> greet(@PathVariable String name) {
        return ResponseEntity.ok(Map.of(
                "message", "Hello, " + name + "! Welcome to GoogleAi."
        ));
    }
}
