package com.kv.zeropass.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoControllerTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void health_returnsOk() {
        DemoController controller = new DemoController();
        ResponseEntity<String> response = controller.health();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("OK");
    }

    @Test
    void hello_returnsAnonymous_whenNoAuthentication() {
        DemoController controller = new DemoController();
        ResponseEntity<String> response = controller.hello();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Hello, anonymous");
    }

    @Test
    void hello_returnsUsername_fromSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "alice",
                        "n/a",
                        List.of(() -> "ROLE_USER")
                )
        );

        DemoController controller = new DemoController();
        ResponseEntity<String> response = controller.hello();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Hello, alice");
    }
}
