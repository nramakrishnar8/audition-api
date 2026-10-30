package com.audition;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = AuditionApplication.class)
class AuditionApplicationTests {

    private final ApplicationContext applicationContext;

    @Autowired
    AuditionApplicationTests(final ApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }

    @Test
    void testContextLoads() {
        assertNotNull(applicationContext);
    }
}
