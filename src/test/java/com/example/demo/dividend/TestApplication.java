package com.example.demo.dividend;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan(
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                com.example.demo.dividend.config.SecurityConfig.class,
                com.example.demo.dividend.security.JwtAuthenticationFilter.class
            }
        )
    }
)
public class TestApplication {
}
