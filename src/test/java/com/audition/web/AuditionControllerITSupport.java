package com.audition.web;

import com.audition.AuditionApplication;
import com.audition.service.AuditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = AuditionApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class AuditionControllerITSupport {

    protected final MockMvc mockMvc;

    @MockBean
    protected AuditionService auditionService;

    @Autowired
    protected AuditionControllerITSupport(final MockMvc mockMvc) {
        super();
        this.mockMvc = mockMvc;
    }
}
