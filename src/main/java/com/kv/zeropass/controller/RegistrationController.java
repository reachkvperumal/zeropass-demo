package com.kv.zeropass.controller;

import com.webauthn4j.data.AuthenticatorTransport;
import com.webauthn4j.data.attestation.AttestationObject;
import com.webauthn4j.data.client.CollectedClientData;
import com.webauthn4j.data.extension.client.AuthenticationExtensionsClientOutputs;
import com.webauthn4j.data.extension.client.RegistrationExtensionClientOutput;
import com.webauthn4j.springframework.security.WebAuthnRegistrationRequestValidationResponse;
import com.webauthn4j.springframework.security.WebAuthnRegistrationRequestValidator;
import com.webauthn4j.springframework.security.credential.InMemoryWebAuthnCredentialRecordManager;
import com.webauthn4j.springframework.security.credential.WebAuthnCredentialRecordImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/webauthn")
public class RegistrationController {

    private static final String REGISTERED_RESPONSE = "REGISTERED";

    private static final String ROLE_USER = "ROLE_USER";

    private final WebAuthnRegistrationRequestValidator registrationRequestValidator;
    private final InMemoryWebAuthnCredentialRecordManager credentialRecordManager;

    public RegistrationController(WebAuthnRegistrationRequestValidator registrationRequestValidator,
                                  InMemoryWebAuthnCredentialRecordManager credentialRecordManager) {
        this.registrationRequestValidator = registrationRequestValidator;
        this.credentialRecordManager = credentialRecordManager;
    }

    @PostMapping("/attestation/result")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request,
                                           HttpServletRequest httpServletRequest) {
        try {
            WebAuthnRegistrationRequestValidationResponse response = registrationRequestValidator.validate(
                    httpServletRequest,
                    request.clientDataJSON,
                    request.attestationObject,
                    request.transports,
                    request.clientExtensionsJSON
            );

            CollectedClientData collectedClientData = response.getCollectedClientData();
            AttestationObject attestationObject = response.getAttestationObject();
            AuthenticationExtensionsClientOutputs<RegistrationExtensionClientOutput> clientOutputs = response.getRegistrationExtensionsClientOutputs();
            Set<AuthenticatorTransport> transports = response.getTransports();

            // For demo purposes, use username as both name and userPrincipal.
            Serializable userPrincipal = request.username;

            WebAuthnCredentialRecordImpl record = new WebAuthnCredentialRecordImpl(
                    request.username,
                    userPrincipal,
                    attestationObject,
                    collectedClientData,
                    clientOutputs,
                    transports
            );

            credentialRecordManager.createCredentialRecord(record);

            // Demo convenience: auto-login after successful registration.
            authenticateIntoSession(request.username, httpServletRequest);

            return ResponseEntity.ok(REGISTERED_RESPONSE);
        } catch (RuntimeException e) {
            // WebAuthn4J wraps WebAuthnException to RuntimeException; return message for demo
            String message = e.getMessage() != null ? e.getMessage() : "Registration failed";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    private static void authenticateIntoSession(String username, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
                username,
                "N/A",
                List.of(new SimpleGrantedAuthority(ROLE_USER))
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

    public static class RegistrationRequest {
        public String username;
        public String clientDataJSON;
        public String attestationObject;
        public Set<String> transports;
        public String clientExtensionsJSON;
    }
}
