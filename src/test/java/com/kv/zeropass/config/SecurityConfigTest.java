package com.kv.zeropass.config;

import com.webauthn4j.WebAuthnManager;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void passwordEncoder_isDelegatingAndCanVerifyEncodedPasswords() {
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        assertThat(encoder).isNotNull();

        String encoded = encoder.encode("password");
        assertThat(encoded).isNotBlank();
        assertThat(encoder.matches("password", encoded)).isTrue();
    }

    @Test
    void userDetailsService_usesConfiguredUser() {
        SecurityConfig config = new SecurityConfig();

        DemoUserProperties userProps = new DemoUserProperties();
        userProps.setName("bob");
        userProps.setPassword("p@ssw0rd");

        PasswordEncoder encoder = config.passwordEncoder();
        UserDetailsService uds = config.userDetailsService(userProps, encoder);

        UserDetails user = uds.loadUserByUsername("bob");
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("bob");
        assertThat(encoder.matches("p@ssw0rd", user.getPassword())).isTrue();
        assertThat(user.getAuthorities()).anyMatch(a -> "ROLE_USER".equals(a.getAuthority()));
    }

    @Test
    void corsConfigurationSource_usesAllowedOriginsFromProperties() {
        SecurityConfig config = new SecurityConfig();
        WebAuthnProperties webAuthnProps = new WebAuthnProperties();

        var source = config.corsConfigurationSource(webAuthnProps);
        var corsConfig = source.getCorsConfiguration(new MockHttpServletRequest("GET", "/hello"));

        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOrigins()).contains("http://localhost:8080");
        assertThat(corsConfig.getAllowedMethods()).contains("GET");
    }

    @Test
    void webAuthnManager_isCreated() {
        SecurityConfig config = new SecurityConfig();
        WebAuthnManager manager = config.webAuthnManager();

        assertThat(manager).isNotNull();
    }

    @Test
    void webAuthnCredentialRecordService_isInMemory() {
        SecurityConfig config = new SecurityConfig();
        var manager = config.webAuthnCredentialRecordManager();

        assertThat(manager).isNotNull();
        assertThat(manager.getClass().getSimpleName()).contains("InMemory");
    }
}
