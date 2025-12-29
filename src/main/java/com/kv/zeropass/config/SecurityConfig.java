package com.kv.zeropass.config;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.springframework.security.config.configurers.WebAuthnLoginConfigurer;
import com.webauthn4j.springframework.security.credential.InMemoryWebAuthnCredentialRecordManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties({WebAuthnProperties.class, DemoUserProperties.class})
public class SecurityConfig {

    private static final String LOGIN_ENDPOINT_PATTERN = "/login/**";
    private static final String CORS_MAPPINGS_PATTERN = "/**";

    private static final String DEFAULT_ALLOWED_ORIGIN = "http://localhost:8080";

    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_PUT = "PUT";
    private static final String HTTP_METHOD_DELETE = "DELETE";
    private static final String HTTP_METHOD_OPTIONS = "OPTIONS";

    private static final List<String> DEFAULT_ALLOWED_METHODS = List.of(
            HTTP_METHOD_GET,
            HTTP_METHOD_POST,
            HTTP_METHOD_PUT,
            HTTP_METHOD_DELETE,
            HTTP_METHOD_OPTIONS
    );

    private static final String WILDCARD = "*";

    private static final String DEFAULT_DEMO_USERNAME = "user";
    private static final String DEFAULT_DEMO_PASSWORD = "password";
    private static final String ROLE_USER = "USER";

    private static final String WEBAUTHN_ENDPOINTS_PATTERN = "/webauthn/**";
    private static final String WEBAUTHN_ATTESTATION_RESULT = "/webauthn/attestation/result";
    private static final String WEBAUTHN_DEBUG_ENDPOINT_PATTERN = "/webauthn/debug/**";
    private static final String WEBAUTHN_ATTESTATION_OPTIONS_INTERNAL_URL = "/webauthn/attestation/options/internal";

    private static final String LOGIN_PAGE_PATH = "/login";
    private static final String LOGIN_PAGE_STATIC_PATH = "/login.html";
    private static final String WEBAUTHN_LOGIN_PROCESSING_URL = "/login/webauthn";

    private static final String STATIC_RESOURCES_PATTERN = "/*.css";
    private static final String STATIC_JS_RESOURCES_PATTERN = "/*.js";
    private static final String STATIC_HTML_RESOURCES_PATTERN = "/*.html";
    private static final String STATIC_WEBJARS_PATTERN = "/webjars/**";

    private static final String DEFAULT_SUCCESS_URL = "/hello";

    private static final String ROOT_PATH = "/";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, WebAuthnProperties webAuthnProps) throws Exception {
        WebAuthnLoginConfigurer<HttpSecurity> webAuthn = WebAuthnLoginConfigurer.webAuthnLogin()
                .rpId(webAuthnProps.getRpId());

        webAuthn.attestationOptionsEndpoint()
                .processingUrl(WEBAUTHN_ATTESTATION_OPTIONS_INTERNAL_URL)
                .rp()
                .id(webAuthnProps.getRpId())
                .name(webAuthnProps.getRpName())
                .and()
                // Required by browsers: ensure pubKeyCredParams is present and non-empty
                .pubKeyCredParams(
                        new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256),
                        new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS256)
                )
                .and();

        webAuthn.assertionOptionsEndpoint()
                .rpId(webAuthnProps.getRpId())
                .and();

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource(webAuthnProps)))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(STATIC_RESOURCES_PATTERN).permitAll()
                        .requestMatchers(STATIC_JS_RESOURCES_PATTERN).permitAll()
                        .requestMatchers(STATIC_HTML_RESOURCES_PATTERN).permitAll()
                        .requestMatchers(STATIC_WEBJARS_PATTERN).permitAll()
                        .requestMatchers(ROOT_PATH).permitAll()
                        .requestMatchers(LOGIN_ENDPOINT_PATTERN).permitAll()
                        .requestMatchers(LOGIN_PAGE_PATH).permitAll()
                        .requestMatchers(LOGIN_PAGE_STATIC_PATH).permitAll()
                        // IMPORTANT: put the more specific matcher first
                        .requestMatchers(WEBAUTHN_DEBUG_ENDPOINT_PATTERN).authenticated()
                        .requestMatchers(WEBAUTHN_ATTESTATION_RESULT).permitAll()
                        .requestMatchers(WEBAUTHN_ENDPOINTS_PATTERN).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage(LOGIN_PAGE_PATH)
                        .defaultSuccessUrl(DEFAULT_SUCCESS_URL, true)
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .with(webAuthn, c -> c
                        .loginPage(LOGIN_PAGE_PATH)
                        .loginProcessingUrl(WEBAUTHN_LOGIN_PROCESSING_URL)
                        .successForwardUrl(DEFAULT_SUCCESS_URL)
                );

        return http.build();
    }

    @Bean
    public InMemoryWebAuthnCredentialRecordManager webAuthnCredentialRecordManager() {
        return new InMemoryWebAuthnCredentialRecordManager();
    }


    @Bean
    public WebAuthnManager webAuthnManager() {
        return WebAuthnManager.createNonStrictWebAuthnManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(WebAuthnProperties props) {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = props.getAllowedOrigins() != null ? props.getAllowedOrigins()
                : List.of(DEFAULT_ALLOWED_ORIGIN);
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(DEFAULT_ALLOWED_METHODS);
        config.setAllowedHeaders(List.of(WILDCARD));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(CORS_MAPPINGS_PATTERN, config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(DemoUserProperties demoUserProperties, PasswordEncoder passwordEncoder) {
        String username = demoUserProperties.getName() != null ? demoUserProperties.getName() : DEFAULT_DEMO_USERNAME;
        String password = demoUserProperties.getPassword() != null ? demoUserProperties.getPassword() : DEFAULT_DEMO_PASSWORD;

        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(ROLE_USER)
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}