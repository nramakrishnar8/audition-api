package com.audition.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {

    private final Headers headers;
    private final Jackson jackson;

    public AppProperties(
            @Valid @NotNull final Headers headers,
            @Valid @NotNull final Jackson jackson) {
        super();
        this.headers = ApiProperties.requireValue(headers, "app.headers");
        this.jackson = ApiProperties.requireValue(jackson, "app.jackson");
    }

    public String traceIdHeader() {
        return headers.traceId;
    }

    public String spanIdHeader() {
        return headers.spanId;
    }

    public String dateFormat() {
        return jackson.dateFormat;
    }

    @Validated
    public static final class Headers {

        private final String traceId;
        private final String spanId;

        public Headers(@NotBlank final String traceId, @NotBlank final String spanId) {
            super();
            this.traceId = ApiProperties.requireText(traceId, "app.headers.trace-id");
            this.spanId = ApiProperties.requireText(spanId, "app.headers.span-id");
        }
    }

    @Validated
    public static final class Jackson {

        private final String dateFormat;

        public Jackson(@NotBlank final String dateFormat) {
            super();
            this.dateFormat = ApiProperties.requireText(dateFormat, "app.jackson.date-format");
        }
    }
}
