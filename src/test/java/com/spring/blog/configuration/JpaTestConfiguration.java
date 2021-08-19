package com.spring.blog.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@TestConfiguration
public class JpaTestConfiguration {
}
