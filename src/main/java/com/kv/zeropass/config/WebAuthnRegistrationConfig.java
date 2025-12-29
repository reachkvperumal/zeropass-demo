package com.kv.zeropass.config;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.springframework.security.WebAuthnRegistrationRequestValidator;
import com.webauthn4j.springframework.security.challenge.ChallengeRepository;
import com.webauthn4j.springframework.security.challenge.HttpSessionChallengeRepository;
import com.webauthn4j.springframework.security.server.ServerPropertyProvider;
import com.webauthn4j.springframework.security.server.ServerPropertyProviderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebAuthnRegistrationConfig {

    @Bean
    public ChallengeRepository challengeRepository() {
        return new HttpSessionChallengeRepository();
    }

    @Bean
    public ServerPropertyProvider serverPropertyProvider(ChallengeRepository challengeRepository, WebAuthnProperties webAuthnProperties) {
        ServerPropertyProviderImpl provider = new ServerPropertyProviderImpl(challengeRepository);
        provider.setRpId(webAuthnProperties.getRpId());
        return provider;
    }

    @Bean
    public WebAuthnRegistrationRequestValidator webAuthnRegistrationRequestValidator(WebAuthnManager webAuthnManager,
                                                                                    ServerPropertyProvider serverPropertyProvider) {
        return new WebAuthnRegistrationRequestValidator(webAuthnManager, serverPropertyProvider);
    }
}
