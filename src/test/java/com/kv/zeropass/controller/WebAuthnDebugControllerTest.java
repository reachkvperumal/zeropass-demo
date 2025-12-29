package com.kv.zeropass.controller;

import com.kv.zeropass.ZeroPassDemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ZeroPassDemoApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.security.user.name=disabled",
        "spring.security.user.password=disabled",
        "app.security.user.name=user",
        "app.security.user.password=password"
})
class WebAuthnDebugControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void debugCredentials_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/webauthn/debug/credentials"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debugCredentials_challengesWithBasicAuth() throws Exception {
        mockMvc.perform(get("/webauthn/debug/credentials")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", org.hamcrest.Matchers.containsString("Basic")));
    }
}
