package com.kv.zeropass.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DemoUserPropertiesTest {

    @Test
    void gettersAndSetters_work() {
        DemoUserProperties props = new DemoUserProperties();
        props.setName("alice");
        props.setPassword("secret");

        assertThat(props.getName()).isEqualTo("alice");
        assertThat(props.getPassword()).isEqualTo("secret");
    }
}

