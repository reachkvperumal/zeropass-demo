package com.kv.zeropass.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SecurityFilterChainIntegrationTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void securityFilterChain_isCreated() {
        assertThat(securityFilterChain).isNotNull();
    }
}

