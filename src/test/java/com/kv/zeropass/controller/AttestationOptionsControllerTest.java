package com.kv.zeropass.controller;

import com.kv.zeropass.ZeroPassDemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ZeroPassDemoApplication.class)
@AutoConfigureMockMvc
class AttestationOptionsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void attestationOptions_includesUserEntity_whenUsernameProvided() throws Exception {
        mockMvc.perform(post("/webauthn/attestation/options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.name").value("alice"))
                .andExpect(jsonPath("$.user.displayName").value("alice"))
                .andExpect(jsonPath("$.user.id").isNotEmpty())
                .andExpect(jsonPath("$.pubKeyCredParams").isArray());
    }
}

