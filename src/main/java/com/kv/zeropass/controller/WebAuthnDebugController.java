package com.kv.zeropass.controller;

import com.webauthn4j.springframework.security.credential.InMemoryWebAuthnCredentialRecordManager;
import com.webauthn4j.springframework.security.credential.WebAuthnCredentialRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/webauthn")
public class WebAuthnDebugController {

    private static final String DEFAULT_USERNAME = "user";

    private final InMemoryWebAuthnCredentialRecordManager credentialRecordManager;

    public WebAuthnDebugController(InMemoryWebAuthnCredentialRecordManager credentialRecordManager) {
        this.credentialRecordManager = credentialRecordManager;
    }

    @GetMapping("/debug/credentials")
    public ResponseEntity<CredentialStatusResponse> credentialsExist(@RequestParam(name = "username", required = false) String username) {
        String effectiveUsername = (username == null || username.isBlank()) ? DEFAULT_USERNAME : username;

        List<WebAuthnCredentialRecord> records = credentialRecordManager.loadCredentialRecordsByUserPrincipal(effectiveUsername);
        boolean exists = records != null && !records.isEmpty();

        return ResponseEntity.ok(new CredentialStatusResponse(effectiveUsername, exists, exists ? records.size() : 0));
    }

    public record CredentialStatusResponse(String username, boolean exists, int count) {
    }
}

