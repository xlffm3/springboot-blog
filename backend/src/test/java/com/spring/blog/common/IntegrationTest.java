package com.spring.blog.common;

import com.spring.blog.configuration.InfrastructureTestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import(InfrastructureTestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class IntegrationTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }
}
