package com.kv.zeropass.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DemoController.BASE_PATH)
public class DemoController {

    public static final String BASE_PATH = "/";
    public static final String HEALTH_PATH = "/health";
    public static final String HELLO_PATH = "/hello";

    private static final String DEFAULT_ANONYMOUS_NAME = "anonymous";

    @GetMapping(HEALTH_PATH)
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping(HELLO_PATH)
    public ResponseEntity<String> hello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String name = DEFAULT_ANONYMOUS_NAME;
        if (authentication != null && authentication.isAuthenticated() && authentication.getName() != null) {
            name = authentication.getName();
        }

        return ResponseEntity.ok("Hello, " + name);
    }
}
