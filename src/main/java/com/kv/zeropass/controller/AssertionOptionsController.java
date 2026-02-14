package com.kv.zeropass.controller;

import com.kv.zeropass.config.WebAuthnProperties;
import com.webauthn4j.springframework.security.challenge.ChallengeRepository;
import com.webauthn4j.springframework.security.credential.WebAuthnCredentialRecordService;
import com.webauthn4j.springframework.security.options.AssertionOptions;
import com.webauthn4j.springframework.security.options.AssertionOptionsProviderImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Custom assertion options endpoint.
 *
 * WebAuthn4J's built-in assertion options endpoint derives allowCredentials from
 * the current Authentication. During passkey login the user is not yet authenticated,
 * so the built-in endpoint returns an empty allowCredentials list. This controller
 * creates a synthetic Authentication from the provided username so the server can
 * look up registered credentials and include them in the response.
 */
@RestController
@RequestMapping("/webauthn")
public class AssertionOptionsController {

    private static final String ERROR_USERNAME_REQUIRED = "username is required";

    private final AssertionOptionsProviderImpl assertionOptionsProvider;

    public AssertionOptionsController(WebAuthnProperties props,
                                      WebAuthnCredentialRecordService credentialRecordService,
                                      ChallengeRepository challengeRepository) {
        this.assertionOptionsProvider = new AssertionOptionsProviderImpl(credentialRecordService, challengeRepository);
        this.assertionOptionsProvider.setRpId(props.getRpId());
    }

    @PostMapping("/assertion/options")
    public ResponseEntity<?> assertionOptions(@RequestBody AttestationOptionsController.UsernameRequest body,
                                              HttpServletRequest request) {
        String username = body != null ? body.username : null;
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ERROR_USERNAME_REQUIRED);
        }

        // Create a synthetic Authentication so the provider can look up credentials by username
        Authentication synthetic = UsernamePasswordAuthenticationToken
                .authenticated(username, "N/A", List.of(() -> "ROLE_USER"));

        AssertionOptions options = assertionOptionsProvider.getAssertionOptions(request, synthetic);
        return ResponseEntity.ok(options);
    }
}
