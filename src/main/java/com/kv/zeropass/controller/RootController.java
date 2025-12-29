package com.kv.zeropass.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Convenience redirect so that visiting http://localhost:8080/ lands on the login page.
 */
@Controller
public class RootController {

    private static final String LOGIN_REDIRECT = "redirect:/login";

    @GetMapping("/")
    public String root() {
        return LOGIN_REDIRECT;
    }
}

