package com.audition;

import com.audition.common.logging.AuditionLogger;
import com.audition.configuration.ApiProperties;
import com.audition.configuration.AppProperties;
import com.audition.configuration.MessageProperties;
import com.audition.configuration.ResponseHeaderInjector;
import com.audition.configuration.WebServiceConfiguration;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.service.AuditionService;
import com.audition.web.AuditionController;
import com.audition.web.AuditionRequestValidator;
import com.audition.web.advice.ExceptionControllerAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableConfigurationProperties({ApiProperties.class, AppProperties.class, MessageProperties.class})
@Import({
        WebServiceConfiguration.class,
        AuditionController.class,
        AuditionRequestValidator.class,
        ExceptionControllerAdvice.class,
        AuditionService.class,
        AuditionIntegrationClient.class,
        ResponseHeaderInjector.class,
        AuditionLogger.class
})
public class AuditionApplication {

    public static void main(final String[] args) {
        SpringApplication.run(AuditionApplication.class, args);
    }

}
