package com.kv.zeropass.config;

import com.webauthn4j.springframework.security.WebAuthnRegistrationRequestValidator;
import com.webauthn4j.springframework.security.challenge.ChallengeRepository;
import com.webauthn4j.springframework.security.server.ServerPropertyProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebAuthnRegistrationConfigTest {

    @Test
    void beans_areCreated() {
        WebAuthnRegistrationConfig config = new WebAuthnRegistrationConfig();

        WebAuthnProperties props = new WebAuthnProperties();
        ChallengeRepository challengeRepository = config.challengeRepository();
        ServerPropertyProvider serverPropertyProvider = config.serverPropertyProvider(challengeRepository, props);

        assertThat(challengeRepository).isNotNull();
        assertThat(serverPropertyProvider).isNotNull();

        // only verify validator can be constructed with required collaborators (WebAuthnManager comes from SecurityConfig)
        WebAuthnRegistrationRequestValidator validator = config.webAuthnRegistrationRequestValidator(
                new SecurityConfig().webAuthnManager(),
                serverPropertyProvider
        );
        assertThat(validator).isNotNull();
    }
}

