package com.kv.zeropass.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.user")
public class DemoUserProperties {

    /**
     * Username for the demo in-memory user.
     */
    private String name;

    /**
     * Raw password for the demo in-memory user.
     */
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

