package com.kv.zeropass.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "webauthn")
public class WebAuthnProperties {
    /**
     * Relying party id (rpId) for passkey / webauthn, e.g. "localhost"
     */
    private String rpId = "localhost";

    /**
     * Relying party name (rpName) shown to users
     */
    private String rpName = "zero pass demo";

    /**
     * Allowed origins for WebAuthn and CORS
     */
    private List<String> allowedOrigins = List.of("http://localhost:8080");

    public String getRpId() {
        return rpId;
    }

    public void setRpId(String rpId) {
        this.rpId = rpId;
    }

    public String getRpName() {
        return rpName;
    }

    public void setRpName(String rpName) {
        this.rpName = rpName;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
