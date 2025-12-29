package com.kv.zeropass.controller;

import com.webauthn4j.springframework.security.WebAuthnRegistrationRequestValidator;
import com.webauthn4j.springframework.security.credential.InMemoryWebAuthnCredentialRecordManager;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RegistrationControllerTest {

    @Test
    void register_returnsBadRequest_whenValidatorThrows() {
        WebAuthnRegistrationRequestValidator validator = mock(WebAuthnRegistrationRequestValidator.class);
        InMemoryWebAuthnCredentialRecordManager manager = new InMemoryWebAuthnCredentialRecordManager();

        when(validator.validate(any(), anyString(), anyString(), any(), any())).thenThrow(new IllegalArgumentException("boom"));

        RegistrationController controller = new RegistrationController(validator, manager);
        RegistrationController.RegistrationRequest req = new RegistrationController.RegistrationRequest();
        req.username = "alice";
        req.clientDataJSON = "x";
        req.attestationObject = "y";

        ResponseEntity<String> response = controller.register(req, new MockHttpServletRequest());

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).contains("boom");

        verify(validator, times(1)).validate(any(), anyString(), anyString(), any(), any());
    }
}
