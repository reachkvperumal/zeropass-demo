package com.kv.zeropass.controller;

import com.kv.zeropass.config.WebAuthnProperties;
import com.webauthn4j.data.AuthenticatorAttachment;
import com.webauthn4j.data.AuthenticatorSelectionCriteria;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.PublicKeyCredentialUserEntity;
import com.webauthn4j.data.ResidentKeyRequirement;
import com.webauthn4j.data.UserVerificationRequirement;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.springframework.security.challenge.ChallengeRepository;
import com.webauthn4j.springframework.security.credential.WebAuthnCredentialRecordService;
import com.webauthn4j.springframework.security.options.AttestationOptions;
import com.webauthn4j.springframework.security.options.AttestationOptionsProviderImpl;
import com.webauthn4j.springframework.security.options.PublicKeyCredentialUserEntityProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Custom attestation options endpoint for demos.
 *
 * WebAuthn requires PublicKeyCredentialCreationOptions.user to be present.
 * WebAuthn4J's default implementation derives it from the current Authentication.
 * During initial registration the user is not authenticated yet, so we build the
 * PublicKeyCredentialUserEntity from the provided username.
 */
@RestController
@RequestMapping("/webauthn")
public class AttestationOptionsController {

    private static final String ERROR_USERNAME_REQUIRED = "username is required";

    private final AttestationOptionsProviderImpl attestationOptionsProvider;

    public AttestationOptionsController(WebAuthnProperties props,
                                       WebAuthnCredentialRecordService credentialRecordService,
                                       ChallengeRepository challengeRepository) {
        this.attestationOptionsProvider = new AttestationOptionsProviderImpl(credentialRecordService, challengeRepository);

        // Ensure RP info is set
        this.attestationOptionsProvider.setRpId(props.getRpId());
        this.attestationOptionsProvider.setRpName(props.getRpName());

        // Ensure required pubKeyCredParams is set
        this.attestationOptionsProvider.setPubKeyCredParams(List.of(
                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256),
                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS256)
        ));

        // Safari tends to be pickier; explicitly prefer a platform authenticator (Touch ID / Face ID)
        // and request discoverable credentials.
        AuthenticatorSelectionCriteria selectionCriteria = new AuthenticatorSelectionCriteria(
                AuthenticatorAttachment.PLATFORM,
                null,
                ResidentKeyRequirement.PREFERRED,
                UserVerificationRequirement.PREFERRED
        );
        this.attestationOptionsProvider.setRegistrationAuthenticatorSelection(selectionCriteria);

        // Provide user entity from Authentication when available; if not, we will supply a synthetic Authentication.
        this.attestationOptionsProvider.setPublicKeyCredentialUserEntityProvider(new PublicKeyCredentialUserEntityProvider() {
            @Override
            public PublicKeyCredentialUserEntity provide(Authentication authentication) {
                if (authentication == null || authentication.getName() == null) {
                    return null;
                }
                String name = authentication.getName();
                return new PublicKeyCredentialUserEntity(name.getBytes(StandardCharsets.UTF_8), name, name);
            }
        });
    }

    @PostMapping("/attestation/options")
    public ResponseEntity<?> attestationOptions(@RequestBody UsernameRequest body, HttpServletRequest request) {
        String username = body != null ? body.username : null;
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ERROR_USERNAME_REQUIRED);
        }

        // Create a synthetic Authentication so AttestationOptionsProviderImpl will populate "user"
        Authentication synthetic = org.springframework.security.authentication.UsernamePasswordAuthenticationToken
                .authenticated(username, "N/A", List.of(() -> "ROLE_USER"));

        AttestationOptions options = attestationOptionsProvider.getAttestationOptions(request, synthetic);
        return ResponseEntity.ok(options);
    }

    public static class UsernameRequest {
        public String username;
    }
}
