package com.kv.zeropass.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WebAuthnPropertiesTest {

    @Test
    void defaults_areSet() {
        WebAuthnProperties props = new WebAuthnProperties();

        assertThat(props.getRpId()).isEqualTo("localhost");
        assertThat(props.getRpName()).isEqualTo("zero pass demo");
        assertThat(props.getAllowedOrigins()).containsExactly("http://localhost:8080");
    }

    @Test
    void setters_updateValues() {
        WebAuthnProperties props = new WebAuthnProperties();
        props.setRpId("example.com");
        props.setRpName("Example RP");
        props.setAllowedOrigins(List.of("http://example.com"));

        assertThat(props.getRpId()).isEqualTo("example.com");
        assertThat(props.getRpName()).isEqualTo("Example RP");
        assertThat(props.getAllowedOrigins()).containsExactly("http://example.com");
    }
}

