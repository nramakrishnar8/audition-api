package com.audition.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.observation.ObservationRegistry;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebServiceConfiguration {

    private static final Logger LOG =
            LoggerFactory.getLogger(WebServiceConfiguration.class);
    private static final String OUTBOUND_REQUEST = "Outbound HTTP request: method={}, uri={}";
    private static final String OUTBOUND_RESPONSE = "Outbound HTTP response: uri={}, status={}";
    private static final String OUTBOUND_FAILED = "Outbound HTTP request failed: uri={}, reason={}";

    private final ApiProperties apiProperties;
    private final AppProperties appProperties;

    public WebServiceConfiguration(
            final ApiProperties apiProperties,
            final AppProperties appProperties) {
        super();
        this.apiProperties = apiProperties;
        this.appProperties = appProperties;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .defaultDateFormat(
                        new SimpleDateFormat(appProperties.dateFormat(), Locale.ROOT))
                .build();
    }

    @Bean
    public RestTemplate restTemplate(
            final ObjectMapper objectMapper,
            final ObjectProvider<ObservationRegistry> observationRegistryProvider) {

        final RestTemplate restTemplate = new RestTemplate(
                new BufferingClientHttpRequestFactory(createClientFactory()));

        restTemplate.setObservationRegistry(
                observationRegistryProvider.getIfAvailable(
                        ObservationRegistry::create));

        restTemplate.getMessageConverters().removeIf(
                converter -> converter instanceof MappingJackson2HttpMessageConverter);

        restTemplate.getMessageConverters().add(
                new MappingJackson2HttpMessageConverter(objectMapper));

        final ClientHttpRequestInterceptor loggingInterceptor =
                (request, body, execution) -> {

                    if (LOG.isInfoEnabled()) {
                        LOG.info(OUTBOUND_REQUEST,
                                request.getMethod(), request.getURI());
                    }

                    try {
                        final var response = execution.execute(request, body);

                        if (LOG.isInfoEnabled()) {
                            LOG.info(OUTBOUND_RESPONSE,
                                    request.getURI(), response.getStatusCode());
                        }

                        return response;

                    } catch (final IOException exception) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn(OUTBOUND_FAILED,
                                    request.getURI(), exception.getMessage());
                        }

                        throw exception;
                    }
                };

        restTemplate.setInterceptors(List.of(loggingInterceptor));

        return restTemplate;
    }

    private SimpleClientHttpRequestFactory createClientFactory() {
        final SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setOutputStreaming(false);
        requestFactory.setConnectTimeout(apiProperties.connectTimeoutMillis());
        requestFactory.setReadTimeout(apiProperties.readTimeoutMillis());

        return requestFactory;
    }
}
