package com.easy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/cors")
    public ResponseEntity<Map<String, String>> testCors() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "CORS is working!");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cors")
    public ResponseEntity<Map<String, String>> testCorsPost(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "POST CORS is working!");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("received", body != null ? body.toString() : "null");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/cors", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
}
