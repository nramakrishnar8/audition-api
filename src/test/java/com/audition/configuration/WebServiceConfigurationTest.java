package com.audition.configuration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.audition.support.TestProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.observation.ObservationRegistry;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

class WebServiceConfigurationTest {

    private final WebServiceConfiguration configuration;

    WebServiceConfigurationTest() {
        super();
        this.configuration = new WebServiceConfiguration(
                TestProperties.apiProperties(Duration.ofSeconds(2), Duration.ofSeconds(3)),
                TestProperties.appProperties());
    }

    private ObjectProvider<ObservationRegistry> observationRegistryProvider() {
        final DefaultListableBeanFactory beanFactory =
                new DefaultListableBeanFactory();

        beanFactory.registerSingleton(
                "observationRegistry",
                ObservationRegistry.create());

        return beanFactory.getBeanProvider(ObservationRegistry.class);
    }

    @Test
    void testObjectMapperIgnoresUnknownProperties() {
        final ObjectMapper mapper = configuration.objectMapper();

        assertFalse(
                mapper.isEnabled(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));

        assertDoesNotThrow(
                () -> mapper.readTree("{\"unknown\":\"value\"}"));
    }

    @Test
    void testObjectMapperOmitsEmptyValuesAndUsesDateStrings() {
        final ObjectMapper mapper = configuration.objectMapper();

        assertNotNull(mapper.getSerializationConfig());
        assertNotNull(mapper.getDateFormat());
        assertFalse(
                mapper.getSerializationConfig().isEnabled(
                        com.fasterxml.jackson.databind.SerializationFeature
                                .WRITE_DATES_AS_TIMESTAMPS));
    }

    @Test
    void testRestTemplateHasConfiguredMessageConverterAndInterceptor() {
        final RestTemplate restTemplate = configuration.restTemplate(
                configuration.objectMapper(),
                observationRegistryProvider());

        assertNotNull(restTemplate);
        assertEquals(1, restTemplate.getInterceptors().size());
        assertNotNull(restTemplate.getRequestFactory());
    }

    @Test
    void testLoggingInterceptorReturnsSuccessfulResponse() throws IOException {
        final RestTemplate restTemplate = configuration.restTemplate(
                configuration.objectMapper(),
                observationRegistryProvider());

        final ClientHttpRequestInterceptor interceptor =
                restTemplate.getInterceptors().get(0);

        final ClientHttpRequest request = mock(ClientHttpRequest.class);
        final ClientHttpRequestExecution execution =
                mock(ClientHttpRequestExecution.class);

        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(
                URI.create(TestProperties.EXAMPLE_POSTS_URI));

        try (ClientHttpResponse response = mock(ClientHttpResponse.class)) {
            when(execution.execute(
                    org.mockito.ArgumentMatchers.any(ClientHttpRequest.class),
                    org.mockito.ArgumentMatchers.any(byte[].class)))
                    .thenReturn(response);

            when(response.getStatusCode()).thenReturn(HttpStatus.OK);

            assertSame(
                    response,
                    interceptor.intercept(request, new byte[0], execution));
        }
    }

    @Test
    void testLoggingInterceptorRethrowsIoException() throws IOException {
        final RestTemplate restTemplate = configuration.restTemplate(
                configuration.objectMapper(),
                observationRegistryProvider());

        final ClientHttpRequestInterceptor interceptor =
                restTemplate.getInterceptors().get(0);

        final ClientHttpRequest request = mock(ClientHttpRequest.class);
        final ClientHttpRequestExecution execution =
                mock(ClientHttpRequestExecution.class);

        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(
                URI.create(TestProperties.EXAMPLE_POSTS_URI));

        when(execution.execute(
                org.mockito.ArgumentMatchers.any(ClientHttpRequest.class),
                org.mockito.ArgumentMatchers.any(byte[].class)))
                .thenThrow(new IOException("Connection failed"));

        assertThrows(
                IOException.class,
                () -> interceptor.intercept(request, new byte[0], execution));
    }
}
