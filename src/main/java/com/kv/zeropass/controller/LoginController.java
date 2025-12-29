package com.kv.zeropass.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the demo login page at "/login" so Spring Security can redirect here.
 *
 * We keep the actual page as a static resource: src/main/resources/static/login.html
 */
@Controller
public class LoginController {

    private static final String LOGIN_PAGE_FORWARD = "forward:/login.html";

    @GetMapping("/login")
    public String login() {
        return LOGIN_PAGE_FORWARD;
    }
}

