package com.kv.zeropass.controller;

import com.kv.zeropass.ZeroPassDemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ZeroPassDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginPageIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void loginPage_isAccessible_andContainsPasskeyButton() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/login.html", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Use passkey");
    }
}
