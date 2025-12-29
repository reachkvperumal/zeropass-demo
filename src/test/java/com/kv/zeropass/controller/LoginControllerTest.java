package com.kv.zeropass.controller;

import com.kv.zeropass.ZeroPassDemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ZeroPassDemoApplication.class)
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void loginEndpoint_forwardsToStaticLoginHtml() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/login.html"));
    }
}
